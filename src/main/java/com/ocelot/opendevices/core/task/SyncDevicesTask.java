package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.devicemanager.ClientDeviceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

@Task.Register(OpenDevices.MOD_ID + ":sync_Devices")
public class SyncDevicesTask extends Task
{
    private ListNBT devices;

    public SyncDevicesTask()
    {
        this(null);
    }

    public SyncDevicesTask(ListNBT devices)
    {
        this.devices = devices;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.put("devices", this.devices);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.devices = nbt.getList("data", Constants.NBT.TAG_COMPOUND);

        if (world.isRemote())
        {
            ClientDeviceManager.INSTANCE.receiveDevices(this.devices);
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
