package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.LaptopDesktop;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.api.laptop.window.application.ApplicationManager;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@TaskManager.Register(OpenDevices.MOD_ID + ":open_window")
public class OpenWindowTask extends Task
{
    private BlockPos pos;
    private LaptopWindow window;

    public OpenWindowTask()
    {
        this(null, null);
    }

    public OpenWindowTask(BlockPos pos, LaptopWindow window)
    {
        this.pos = pos;
        this.window = window;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.put("data", this.window.serializeNBT());

        CompoundNBT stateNbt = new CompoundNBT();
        this.window.saveState(stateNbt);
        nbt.put("state", stateNbt);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            LaptopDesktop desktop = laptop.getDesktop();

            this.window = desktop.createWindow(nbt.getCompound("data"), nbt.getCompound("state"));
            if (ApplicationManager.isValidApplication(this.window.getContentId()))
            {
                desktop.syncOpenWindow(this.window);
                this.setSuccessful();
            }
            else
            {
                OpenDevices.LOGGER.error("Attempted to open unregistered window: " + this.window.getContentId() + ". Applications must be registered using the WindowContent#Register annotation.");
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
