package com.ocelot.opendevices.container;

import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.crafting.ComponentBuilderRecipe;
import com.ocelot.opendevices.init.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ComponentBuilderContainer extends Container
{
    private ComponentBuilderBoardLayout layout;
    private IWorldPosCallable posCallable;
    private PlayerEntity player;

    private IInventory inputArea = new Inventory(3)
    {
        @Override
        public void markDirty()
        {
            if (!hasCircuitBoard())
            {
                clearContainer(player, player.world, craftingArea);
                detectAndSendChanges();
            }
            ComponentBuilderContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };
    private IInventory craftingArea = new CraftingInventory(this, 3, 3);
    private CraftResultInventory craftingResult = new CraftResultInventory()
    {
        @Override
        public void markDirty()
        {
            if (!hasCircuitBoard())
            {
                clearContainer(player, player.world, craftingArea);
                detectAndSendChanges();
            }
            ComponentBuilderContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };

    public ComponentBuilderContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public ComponentBuilderContainer(int id, PlayerInventory playerInventory, IWorldPosCallable posCallable)
    {
        super(DeviceContainers.COMPONENT_BUILDER, id);
        this.layout = DeviceBoardLayouts.CENTER;
        this.posCallable = posCallable;
        this.player = playerInventory.player;

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                this.addSlot(new Slot(this.craftingArea, x + y * 3, 10 + x * 21, 20 + y * 21)
                {
                    @Override
                    public boolean isEnabled()
                    {
                        return this.getHasStack() || (ComponentBuilderContainer.this.hasCircuitBoard() && ComponentBuilderContainer.this.layout.hasSlot(1 << this.getSlotIndex()));
                    }
                });
            }
        }

        this.addSlot(new Slot(this.inputArea, 0, 114, 28)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isIn(DeviceTags.CIRCUIT_BOARDS);
            }
        });

        this.addSlot(new Slot(this.inputArea, 1, 146, 18)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == DeviceItems.SOLDER_IRON;
            }
        });

        this.addSlot(new Slot(this.inputArea, 2, 146, 40)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == DeviceItems.SOLDER;
            }
        });

        this.addSlot(new Slot(this.craftingResult, 0, 114, 54)
        {
            private int removeCount;

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }

            @Override
            public ItemStack decrStackSize(int amount)
            {
                if (this.getHasStack())
                {
                    this.removeCount += Math.min(amount, this.getStack().getCount());
                }

                return super.decrStackSize(amount);
            }

            @Override
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
            {
                this.onCrafting(stack);
                super.onTake(thePlayer, stack);
                return stack;
            }

            @Override
            protected void onCrafting(ItemStack stack, int amount)
            {
                this.removeCount += amount;
                this.onCrafting(stack);
            }

            @Override
            protected void onCrafting(ItemStack stack)
            {
                stack.onCrafting(player.world, player, this.removeCount);
                ComponentBuilderRecipe recipe = craftingResult.getRecipeUsed() instanceof ComponentBuilderRecipe ? ((ComponentBuilderRecipe) craftingResult.getRecipeUsed()) : null;

                if (recipe != null && canCraft(recipe))
                {
                    if (!player.world.isRemote())
                    {
                        ItemStack result = inputArea.getStackInSlot(1).copy();
                        result.damageItem(this.removeCount, player, t -> player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F));
                        inputArea.setInventorySlotContents(1, result);
                        detectAndSendChanges();
                    }

                    for (int i = 0; i < craftingArea.getSizeInventory(); i++)
                    {
                        craftingArea.decrStackSize(i, this.removeCount);
                    }

                    if (!player.isCreative())
                    {
                        inputArea.decrStackSize(0, this.removeCount);
                        inputArea.decrStackSize(2, this.removeCount * recipe.getSolderAmount());
                    }
                }

                this.removeCount = 0;
            }
        });

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 94 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 152));
        }
    }

    public void setLayout(ComponentBuilderBoardLayout layout)
    {
        if (!this.player.world.isRemote())
        {
            this.clearContainer(this.player, this.player.world, this.craftingArea);
            this.detectAndSendChanges();
        }
        this.layout = layout;
    }

    private static void updateRecipe(int windowId, World world, PlayerEntity player, IInventory craftingArea, CraftResultInventory resultInventory, Function<ComponentBuilderRecipe, Boolean> canCraft)
    {
        resultInventory.setRecipeUsed(null);
        ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
        ItemStack itemstack = ItemStack.EMPTY;
        Optional<ComponentBuilderRecipe> optional = Objects.requireNonNull(world.getServer()).getRecipeManager().getRecipe(DeviceRecipes.COMPONENT_BUILDER, craftingArea, world);
        if (optional.isPresent())
        {
            ComponentBuilderRecipe recipe = optional.get();
            if (canCraft.apply(recipe) && resultInventory.canUseRecipe(world, serverplayerentity, recipe))
            {
                itemstack = recipe.getCraftingResult(craftingArea);
                resultInventory.setRecipeUsed(recipe);
            }
        }

        if (!world.isRemote())
        {
            resultInventory.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 12, itemstack));
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        this.posCallable.consume((world, pos) -> updateRecipe(this.windowId, world, this.player, this.craftingArea, this.craftingResult, this::canCraft));
    }

    private boolean canCraft(ComponentBuilderRecipe recipe)
    {
        ItemStack circuitBoardStack = this.inputArea.getStackInSlot(0);
        ItemStack solderIronStack = this.inputArea.getStackInSlot(1);
        ItemStack solderStack = this.inputArea.getStackInSlot(2);
        return recipe.getLayout() == this.layout && recipe.getRecipeInput().test(circuitBoardStack) && (this.player.isCreative() || solderIronStack.getItem() == DeviceItems.SOLDER_IRON && solderStack.getItem() == DeviceItems.SOLDER && solderStack.getCount() >= recipe.getSolderAmount());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 12)
            {
                this.posCallable.consume((world, pos) -> itemstack1.getItem().onCreated(itemstack1, world, player));
                if (!this.mergeItemStack(itemstack1, 13, 49, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index > 12)
            {
                if (itemstack1.getItem().isIn(DeviceTags.CIRCUIT_BOARDS))
                {
                    if (!this.mergeItemStack(itemstack1, 9, 10, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() == DeviceItems.SOLDER_IRON)
                {
                    if (!this.mergeItemStack(itemstack1, 10, 11, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() == DeviceItems.SOLDER)
                {
                    if (!this.mergeItemStack(itemstack1, 11, 12, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 40)
                {
                    if (!this.mergeItemStack(itemstack1, 40, 49, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 48)
                {
                    if (!this.mergeItemStack(itemstack1, 12, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.mergeItemStack(itemstack1, 12, 49, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 12, 49, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return isWithinUsableDistance(posCallable, player, DeviceBlocks.COMPONENT_BUILDER);
    }

    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);
        this.craftingResult.removeStackFromSlot(0);
        this.posCallable.consume((world, pos) ->
        {
            this.clearContainer(player, player.world, this.inputArea);
            this.clearContainer(player, player.world, this.craftingArea);
        });
    }

    public boolean hasCircuitBoard()
    {
        ItemStack circuitBoardStack = this.inputArea.getStackInSlot(0);
        return !circuitBoardStack.isEmpty() && circuitBoardStack.getItem().isIn(DeviceTags.CIRCUIT_BOARDS);
    }

    public IInventory getInputAreaInventory()
    {
        return inputArea;
    }

    public ComponentBuilderBoardLayout getLayout()
    {
        return layout;
    }
}
