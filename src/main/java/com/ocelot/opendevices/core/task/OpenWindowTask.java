package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.LaptopWindowManager;
import com.ocelot.opendevices.core.computer.LaptopWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":open_window")
public class OpenWindowTask extends Task
{
    private UUID address;
    private CompoundNBT windowData;

    public OpenWindowTask()
    {
        this(null, null);
    }

    public OpenWindowTask(UUID address, CompoundNBT windowData)
    {
        this.address = address;
        this.windowData = windowData;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        nbt.put("windowData", this.windowData);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.windowData = nbt.getCompound("windowData");

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            LaptopWindowManager windowManager = laptop.getWindowManager();
            LaptopWindow window = windowManager.createWindow(this.windowData);
            if (windowManager.syncOpenWindow(window))
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
