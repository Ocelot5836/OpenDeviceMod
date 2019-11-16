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

@TaskManager.Register(OpenDevices.MOD_ID + ":move_window")
public class MoveWindowTask extends Task
{
    private BlockPos pos;
    private UUID windowId;
    private float xDirection;
    private float yDirection;

    public MoveWindowTask()
    {
        this(null, null, 0, 0);
    }

    public MoveWindowTask(BlockPos pos, UUID windowId, float xDirection, float yDirection)
    {
        this.pos = pos;
        this.windowId = windowId;
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("windowId", this.windowId);
        nbt.putDouble("xDirection", this.xDirection);
        nbt.putDouble("yDirection", this.yDirection);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.windowId = nbt.getUniqueId("windowId");
        this.xDirection = nbt.getFloat("xDirection");
        this.yDirection = nbt.getFloat("yDirection");

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            LaptopDesktop desktop = laptop.getDesktop();
            LaptopWindow window = desktop.getWindow(this.windowId);
            if (window != null)
            {
                window.syncMove(this.xDirection, this.yDirection);
            }
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
