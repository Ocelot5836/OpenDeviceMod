package com.ocelot.opendevices.block;

import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.device.DeviceTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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

    /**
     * Randomizes the address of the specified address.
     *
     * @param device The device to randomize
     */
    protected void randomizeAddress(Device device)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote() && te instanceof Device)
        {
            Device device = (Device) te;
            DeviceManager deviceManager = DeviceManager.get(world);
            if (device.getAddress() == null || deviceManager.exists(device.getAddress()))
                this.randomizeAddress(device);
            if (!deviceManager.exists(device.getAddress()))
                deviceManager.add(device, (DeviceSerializer<? super Device>) device.getSerializer());
        }
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
