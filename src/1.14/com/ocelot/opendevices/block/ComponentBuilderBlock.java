package com.ocelot.opendevices.block;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.container.ComponentBuilderContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ComponentBuilderBlock extends DeviceBlock
{
    public static final TranslationTextComponent TITLE = new TranslationTextComponent("container." + OpenDevices.MOD_ID + ".component_builder");

    public ComponentBuilderBlock(String id)
    {
        super(id, Block.Properties.create(Material.WOOD, MaterialColor.BROWN), new Item.Properties().group(OpenDevices.TAB));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        player.openContainer(state.getContainer(world, pos));
        return ActionResultType.SUCCESS;
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos)
    {
        return new SimpleNamedContainerProvider((id, playerInventory, player) -> new ComponentBuilderContainer(id, playerInventory, IWorldPosCallable.of(world, pos)), TITLE);
    }
}
