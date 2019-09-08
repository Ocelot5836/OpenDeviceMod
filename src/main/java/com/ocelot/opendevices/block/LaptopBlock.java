package com.ocelot.opendevices.block;

import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.item.DeviceBlockItem;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;

public class LaptopBlock extends ModBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private DyeColor color;

    public LaptopBlock(DyeColor color)
    {
        super(color.getTranslationKey() + "_laptop", Block.Properties.create(Material.ANVIL, color));
        this.setDefaultState(this.getStateContainer().getBaseState().with(TYPE, Type.BASE));
        DeviceBlocks.register(this, new DeviceBlockItem(this));
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new LaptopTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(TYPE);
    }

    public DyeColor getColor()
    {
        return color;
    }

    public enum Type implements IStringSerializable
    {
        BASE, SCREEN;

        @Override
        public String getName()
        {
            return name().toLowerCase();
        }

    }
}
