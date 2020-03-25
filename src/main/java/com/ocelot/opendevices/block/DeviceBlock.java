package com.ocelot.opendevices.block;

import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.device.DeviceSerializer;
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

    protected void randomizeAddress(Device device, World world, BlockPos pos)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!world.isRemote())
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof Device)
            {
                Device device = (Device) tileEntity;
                DeviceManager deviceManager = DeviceManager.get(world);
                if (deviceManager.exists(device.getAddress()))
                    this.randomizeAddress(device, world, pos);
                deviceManager.add(device, (DeviceSerializer<? super Device>) device.getSerializer());
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            if (!world.isRemote())
            {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof Device)
                {
                    DeviceManager.get(world).remove(((Device) tileEntity).getAddress());
                }
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (this.dropInCreative())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DeviceTileEntity)
            {
                DeviceTileEntity device = (DeviceTileEntity) te;
                if (!world.isRemote && player.isCreative())
                {
                    ItemStack stack = new ItemStack(this);
                    CompoundNBT nbt = new CompoundNBT();
                    device.save(nbt);
                    if (!nbt.isEmpty())
                    {
                        CompoundNBT entityTag = new CompoundNBT();
                        entityTag.put("device", nbt);
                        stack.setTagInfo("BlockEntityTag", entityTag);
                    }

                    if (te instanceof INameable && ((INameable) te).hasCustomName())
                    {
                        stack.setDisplayName(((INameable) te).getCustomName());
                    }

                    ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                    entity.setDefaultPickupDelay();
                    world.addEntity(entity);
                }
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    protected boolean dropInCreative()
    {
        return true;
    }
}
