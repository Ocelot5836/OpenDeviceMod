package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.LaptopWindowManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":focus_window")
public class FocusWindowTask extends Task
{
    private UUID address;
    private UUID windowId;

    public FocusWindowTask()
    {
    }

    public FocusWindowTask(UUID address, @Nullable UUID windowId)
    {
        this.address = address;
        this.windowId = windowId;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        if (this.windowId != null)
        {
            nbt.putUniqueId("windowId", this.windowId);
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.windowId = nbt.hasUniqueId("windowId") ? nbt.getUniqueId("windowId") : null;

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            LaptopWindowManager windowManager = laptop.getWindowManager();
            if (windowManager.syncFocusWindow(this.windowId))
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
