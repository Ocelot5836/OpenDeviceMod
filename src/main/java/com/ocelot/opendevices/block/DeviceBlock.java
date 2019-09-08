package com.ocelot.opendevices.block;

import com.ocelot.opendevices.api.device.DeviceTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DeviceBlock extends ModBlock
{
    public DeviceBlock(Properties properties)
    {
        super(properties);
    }

    public DeviceBlock(String id, Properties properties)
    {
        super(id, properties);
    }

    public DeviceBlock(String id, Properties properties, Item.Properties itemProperties)
    {
        super(id, properties, itemProperties);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return super.canHarvestBlock(state, world, pos, player);
    }

    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DeviceTileEntity) {
            DeviceTileEntity device = (DeviceTileEntity) te;
            if (!world.isRemote && player.isCreative()) {
                ItemStack stack = new ItemStack(this);
                CompoundNBT nbt = new CompoundNBT();
                device.save(nbt);
                if (!nbt.isEmpty()) {
                    CompoundNBT entityTag = new CompoundNBT();
                    entityTag.put("device", nbt);
                    stack.setTagInfo("BlockEntityTag", entityTag);
                }

                if (te instanceof INameable && ((INameable) te).hasCustomName()) {
                    stack.setDisplayName(((INameable) te).getCustomName());
                }

                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                entity.setDefaultPickupDelay();
                world.addEntity(entity);
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }
}
