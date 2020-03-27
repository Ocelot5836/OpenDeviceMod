package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":sync_process")
public class SyncProcessTask extends Task
{
    private UUID address;
    private UUID processId;
    private CompoundNBT data;

    public SyncProcessTask()
    {
        this(null, null, null);
    }

    public SyncProcessTask(UUID address, UUID processId, CompoundNBT data)
    {
        this.address = address;
        this.processId = processId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        nbt.putUniqueId("processId", this.processId);
        nbt.put("data", this.data);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.processId = nbt.getUniqueId("processId");
        this.data = nbt.getCompound("data");

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            if (laptop.getUser() != null && laptop.getUser().getUniqueID().equals(player.getUniqueID()) && laptop.syncProcess(this.processId, this.data))
            {
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
        if (this.isSucessful())
        {
            this.prepareRequest(nbt);
        }
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
        if (this.isSucessful())
        {
            this.processRequest(nbt, world, player);
        }
    }
}
