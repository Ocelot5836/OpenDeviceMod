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

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":move_window")
public class MoveWindowTask extends Task
{
    private UUID address;
    private UUID windowId;
    private float xDirection;
    private float yDirection;

    public MoveWindowTask()
    {
    }

    public MoveWindowTask(UUID address, UUID windowId, float xDirection, float yDirection)
    {
        this.address = address;
        this.windowId = windowId;
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        nbt.putUniqueId("windowId", this.windowId);
        nbt.putDouble("xDirection", this.xDirection);
        nbt.putDouble("yDirection", this.yDirection);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.windowId = nbt.getUniqueId("windowId");
        this.xDirection = nbt.getFloat("xDirection");
        this.yDirection = nbt.getFloat("yDirection");

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            LaptopWindowManager windowManager = laptop.getWindowManager();
            if (windowManager.syncMoveWindow(this.windowId, this.xDirection, this.yDirection))
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
