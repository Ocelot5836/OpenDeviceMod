package com.ocelot.opendevices.block;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ModBlock extends Block
{
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ModBlock(Properties properties)
    {
        super(properties);
    }

    public ModBlock(String registryName, Properties properties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName));
    }

    public ModBlock(String registryName, Properties properties, Item.Properties itemProperties)
    {
        this(registryName, properties);
        DeviceItems.register(DeviceBlocks.createDefaultBlockItem(this, itemProperties));
    }

    public ModBlock(String registryName, Properties properties, Item item)
    {
        this(registryName, properties);
        DeviceItems.register(item);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = this.getDefaultState();
        if (state.has(FACING)) {
            state = state.with(FACING, context.getNearestLookingDirection().getOpposite());
        }
        if (state.has(HORIZONTAL_FACING)) {
            state = state.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        if (state.has(FACING)) {
            state = state.with(FACING, rotation.rotate(state.get(FACING)));
        }
        if (state.has(HORIZONTAL_FACING)) {
            state = state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.has(FACING)) {
            state = state.rotate(mirror.toRotation(state.get(FACING)));
        }
        if (state.has(HORIZONTAL_FACING)) {
            state = state.rotate(mirror.toRotation(state.get(HORIZONTAL_FACING)));
        }
        return state;
    }

    @Override
    public IFluidState getFluidState(BlockState state)
    {
        return state.has(WATERLOGGED) && state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}