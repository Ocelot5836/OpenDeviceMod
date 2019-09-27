package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.window.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@TaskManager.Register(OpenDevices.MOD_ID + ":open_window")
public class OpenWindowTask extends Task
{
    private BlockPos pos;
    private Window window;

    public OpenWindowTask()
    {
        this(null, new Window(0, 0));
    }

    public OpenWindowTask(BlockPos pos, Window window)
    {
        this.pos = pos;
        this.window = window;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.put("window", this.window.serializeNBT());
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.window.deserializeNBT(nbt.getCompound("window"));

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos))).getDesktop().openWindow(this.window);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
        this.prepareRequest(nbt);
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.processRequest(nbt, world, player);
    }
}
