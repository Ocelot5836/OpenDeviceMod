package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.LaptopWindowManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":mark_close_window")
public class RequestCloseWindowTask extends Task
{
    private UUID address;
    private UUID[] windowIds;

    public RequestCloseWindowTask()
    {
    }

    public RequestCloseWindowTask(UUID address, UUID... windowIds)
    {
        this.address = address;
        this.windowIds = windowIds;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);

        ListNBT windowIds = new ListNBT();
        for (UUID windowId : this.windowIds)
        {
            CompoundNBT windowIdNbt = new CompoundNBT();
            windowIdNbt.putUniqueId("id", windowId);
            windowIds.add(windowIdNbt);
        }
        nbt.put("windowIds", windowIds);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");

        ListNBT windowIds = nbt.getList("windowIds", Constants.NBT.TAG_COMPOUND);
        this.windowIds = new UUID[windowIds.size()];
        for (int i = 0; i < windowIds.size(); i++)
        {
            this.windowIds[i] = windowIds.getCompound(i).getUniqueId("id");
        }

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            LaptopWindowManager windowManager = laptop.getWindowManager();
            windowManager.syncRequestCloseWindows(this.windowIds);
            this.setSuccessful();
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
