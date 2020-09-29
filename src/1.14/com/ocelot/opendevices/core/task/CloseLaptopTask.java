package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":close_laptop")
public class CloseLaptopTask extends Task
{
    private UUID address;

    public CloseLaptopTask()
    {
    }

    public CloseLaptopTask(UUID address)
    {
        this.address = address;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            laptop.stopView(player);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
    }
}
