package com.ocelot.opendevices.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.*;
import com.ocelot.opendevices.api.task.TaskManager.Register;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

// TODO move to an example mod
//@TaskManager.Register(OpenDevices.MOD_ID + ":test")
public class TestTask extends Task
{
    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        System.out.println("preparing request");
        nbt.putInt("test", 4);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        System.out.println("processing request: " + nbt.getInt("test"));
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
        if (this.isSucessful())
        {
            System.out.println("preparing response");
            nbt.putInt("test", 12);
        }
    }

    @Override
    public void processResponse(CompoundNBT nbt)
    {
        if (this.isSucessful())
        {
            System.out.println("processing response: " + nbt.getInt("test"));
        }
    }
}
