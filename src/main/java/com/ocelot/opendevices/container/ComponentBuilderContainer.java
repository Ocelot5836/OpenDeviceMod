package com.ocelot.opendevices.container;

import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.crafting.component_builder.ComponentBuilderRecipe;
import com.ocelot.opendevices.init.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.Objects;
import java.util.Optional;

public class ComponentBuilderContainer extends Container
{
    private ComponentBuilderBoardLayout layout;
    private IWorldPosCallable posCallable;
    private PlayerEntity player;
    private IInventory craftingArea = new Inventory(12)
    {
        @Override
        public void markDirty()
        {
            ComponentBuilderContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };

    private CraftResultInventory craftingResult = new CraftResultInventory()
    {
        @Override
        public void markDirty()
        {
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

        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
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

        this.addSlot(new Slot(this.craftingArea, 9, 114, 28)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isIn(DeviceTags.CIRCUIT_BOARDS);
            }
        });

        this.addSlot(new Slot(this.craftingArea, 10, 146, 18)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == DeviceItems.SOLDER_IRON;
            }
        });

        this.addSlot(new Slot(this.craftingArea, 11, 146, 40)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == DeviceItems.SOLDER;
            }
        });

        this.addSlot(new Slot(this.craftingResult, 0, 114, 54)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }

            @Override
            public ItemStack decrStackSize(int amount)
            {
                for (int i = 0; i < 10; i++)
                {
                    ComponentBuilderContainer.this.craftingArea.decrStackSize(i, 1);
                }
                ComponentBuilderContainer.this.posCallable.consume((world, pos) -> ComponentBuilderContainer.this.craftingArea.getStackInSlot(1).attemptDamageItem(10, world.rand, null));
                ComponentBuilderContainer.this.craftingArea.decrStackSize(11, 1); // TODO decrease solder by recipe amount
                return super.decrStackSize(amount);
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
        this.posCallable.consume((world, pos) -> this.clearContainer(this.player, this.player.world, this.craftingArea));
        this.layout = layout;
    }

    protected static void updateRecipe(int windowId, World world, PlayerEntity player, IInventory craftingArea, CraftResultInventory resultInventory)
    {
        if (!world.isRemote)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ComponentBuilderRecipe> optional = Objects.requireNonNull(world.getServer()).getRecipeManager().getRecipe(DeviceRecipes.COMPONENT_BUILDER, craftingArea, world);
            if (optional.isPresent())
            {
                ComponentBuilderRecipe recipe = optional.get();
                if (resultInventory.canUseRecipe(world, serverplayerentity, recipe))
                {
                    itemstack = recipe.getCraftingResult(craftingArea);
                }
            }

            resultInventory.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 12, itemstack));
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        this.posCallable.consume((world, pos) ->
        {
            if (!this.hasCircuitBoard())
            {
                this.clearContainer(this.player, this.player.world, this.craftingArea);
            }
            this.detectAndSendChanges();
            updateRecipe(this.windowId, world, this.player, this.craftingArea, this.craftingResult);
        });
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 12)
            {
                if (!this.mergeItemStack(itemstack1, 12, 49, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else
            {
                if (itemstack1.getItem().isIn(Tags.Items.INGOTS)) // TODO only allow items that are in the recipe in here
                {
                    if (!this.mergeItemStack(itemstack1, 0, 9, false)) // TODO only fill through the slots that can be filled
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem().isIn(DeviceTags.CIRCUIT_BOARDS))
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
                else if (index < 49 && !this.mergeItemStack(itemstack1, 13, 39, false))
                {
                    return ItemStack.EMPTY;
                }
                //                                if (itemstack1.getItem().isIn(DeviceTags.CIRCUIT_BOARDS)) {
                //                                    if (!this.mergeItemStack(itemstack1, 9, 10, false)) {
                //                                        return ItemStack.EMPTY;
                //                                    }
                //                                }else
                //                                if (index >= 3 && index < 30) {
                //                                                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                //                                                        return ItemStack.EMPTY;
                //                                                    }
                //                                                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                //                                                    return ItemStack.EMPTY;
                //                                                }
            }

            //            if (index == 2) {
            //                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
            //                    return ItemStack.EMPTY;
            //                }
            //
            //                slot.onSlotChange(itemstack1, itemstack);
            //            } else if (index != 1 && index != 0) {
            //                if (this.func_217057_a(itemstack1)) {
            //                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
            //                        return ItemStack.EMPTY;
            //                    }
            //                } else if (this.isFuel(itemstack1)) {
            //                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
            //                        return ItemStack.EMPTY;
            //                    }
            //                } else if (index >= 3 && index < 30) {
            //                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
            //                        return ItemStack.EMPTY;
            //                    }
            //                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
            //                    return ItemStack.EMPTY;
            //                }
            //            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            //                return ItemStack.EMPTY;
            //            }

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

            slot.onTake(playerIn, itemstack1);
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
        this.posCallable.consume((world, pos) -> this.clearContainer(player, player.world, this.craftingArea));
    }

    public boolean hasCircuitBoard()
    {
        ItemStack circuitBoardStack = this.craftingArea.getStackInSlot(9);
        return !circuitBoardStack.isEmpty() && circuitBoardStack.getItem().isIn(DeviceTags.CIRCUIT_BOARDS);
    }

    public IInventory getCraftingAreaInventory()
    {
        return craftingArea;
    }

    public ComponentBuilderBoardLayout getLayout()
    {
        return layout;
    }
}
