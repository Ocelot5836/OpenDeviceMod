package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.LaptopDesktop;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

@TaskManager.Register(OpenDevices.MOD_ID + ":set_window_pos")
public class SetWindowPositionTask extends Task
{
    private BlockPos pos;
    private UUID windowId;
    private float x;
    private float y;

    public SetWindowPositionTask()
    {
        this(null, null, 0, 0);
    }

    public SetWindowPositionTask(BlockPos pos, UUID windowId, float x, float y)
    {
        this.pos = pos;
        this.windowId = windowId;
        this.x = x;
        this.y = y;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("windowId", this.windowId);
        nbt.putDouble("x", this.x);
        nbt.putDouble("y", this.y);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.windowId = nbt.getUniqueId("windowId");
        this.x = nbt.getFloat("x");
        this.y = nbt.getFloat("y");

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            LaptopDesktop desktop = laptop.getDesktop();
            LaptopWindow window = desktop.getWindow(this.windowId);
            if (window != null)
            {
                window.syncSetPosition(this.x, this.y);
            }
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