package com.ocelot.opendevices.block;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.INameChangeable;
import com.ocelot.opendevices.api.util.ISimpleInventory;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModBlock extends Block
{
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ModBlock(Block.Properties properties)
    {
        super(properties);
    }

    public ModBlock(String id, Block.Properties properties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, id));
        DeviceBlocks.register(this);
    }

    public ModBlock(String id, Block.Properties properties, Item.Properties itemProperties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, id));
        DeviceBlocks.register(this, itemProperties);
    }

    public boolean hasComparatorInputOverride(BlockState state)
    {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof ISimpleInventory) {
            return calcRedstoneFromInventory((ISimpleInventory) world.getTileEntity(pos));
        }
        if (world.getTileEntity(pos) instanceof IInventory) {
            return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos));
        }
        return 0;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ISimpleInventory) {
                ISimpleInventory inventory = (ISimpleInventory) te;
                if (!inventory.isEmpty()) {
                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));
                    }
                    world.updateComparatorOutputLevel(pos, this);
                }
            }
            if (te instanceof IInventory) {
                InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
                world.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (stack.hasDisplayName()) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof INameChangeable) {
                ((INameChangeable) te).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = this.getDefaultState();
        if (state.has(HORIZONTAL_FACING)) {
            state = state.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
        }
        if (state.has(FACING)) {
            state = state.with(FACING, context.getNearestLookingDirection().getOpposite());
        }
        if (state.has(WATERLOGGED)) {
            state = state.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        if (state.has(HORIZONTAL_FACING)) {
            state = state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
        }
        if (state.has(FACING)) {
            state = state.with(FACING, rotation.rotate(state.get(FACING)));
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.has(HORIZONTAL_FACING)) {
            state.rotate(mirror.toRotation(state.get(HORIZONTAL_FACING)));
        }
        if (state.has(FACING)) {
            state.rotate(mirror.toRotation(state.get(FACING)));
        }
        return state;
    }

    @Override
    public IFluidState getFluidState(BlockState state)
    {
        return state.has(WATERLOGGED) && state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    private static int calcRedstoneFromInventory(@Nullable ISimpleInventory inventory)
    {
        if (inventory == null) {
            return 0;
        }
        else {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < inventory.getSizeInventory(); ++j) {
                ItemStack itemstack = inventory.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    f += (float) itemstack.getCount() / (float) Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
                    ++i;
                }
            }

            f = f / (float) inventory.getSizeInventory();
            return MathHelper.floor(f * 14.0F) + (!inventory.isEmpty() ? 1 : 0);
        }
    }
}
