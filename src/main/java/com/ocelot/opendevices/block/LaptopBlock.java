package com.ocelot.opendevices.block;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.item.DeviceBlockItem;
import com.ocelot.opendevices.proxy.ServerProxy;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LaptopBlock extends DeviceBlock
{
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation(OpenDevices.MOD_ID, "laptop");
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private DyeColor color;

    public LaptopBlock(DyeColor color)
    {
        super(color.getTranslationKey() + "_laptop", Block.Properties.create(Material.MISCELLANEOUS, color).doesNotBlockMovement());
        this.setDefaultState(this.getStateContainer().getBaseState().with(TYPE, Type.BASE));
        DeviceBlocks.register(this, new DeviceBlockItem(this));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.getTileEntity(pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity te = (LaptopTileEntity) world.getTileEntity(pos);
            assert te != null;
            if (player.isSneaking())
            {
                return te.toggleOpen(player);
            }
            else if (te.view(player))
            {
                OpenDevices.PROXY.openGui(player, ServerProxy.GuiType.LAPTOP, pos);
            }
            else
            {
                if (!world.isRemote())
                {
                    PlayerEntity userPlayer = te.getUserPlayer();
                    if (userPlayer != null)
                    {
                        player.sendStatusMessage(new TranslationTextComponent("block." + OpenDevices.MOD_ID + ".laptop.using.specific", userPlayer.getDisplayName().getFormattedText()), true);
                    }
                    else
                    {
                        player.sendStatusMessage(new TranslationTextComponent("block." + OpenDevices.MOD_ID + ".laptop.using"), true);
                    }
                }
            }
            return true;
        }
        return false;
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
