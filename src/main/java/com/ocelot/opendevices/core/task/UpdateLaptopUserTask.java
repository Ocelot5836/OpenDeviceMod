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

@Task.Register(OpenDevices.MOD_ID + ":update_laptop_user")
public class UpdateLaptopUserTask extends Task
{
    private UUID address;
    private UUID user;

    public UpdateLaptopUserTask()
    {
    }

    public UpdateLaptopUserTask(UUID address, UUID user)
    {
        this.address = address;
        this.user = user;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        nbt.putUniqueId("userId", this.user);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.user = nbt.getUniqueId("userId");

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            if (laptop.view(world.getPlayerByUuid(this.user)))
            {
                this.setSuccessful();
            }
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
