package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

@Deprecated
@Task.Register(OpenDevices.MOD_ID + ":set_window_size")
public class SetWindowSizeTask extends Task
{
    private BlockPos pos;
    private UUID windowId;
    private int width;
    private int height;

    public SetWindowSizeTask()
    {
        this(null, null, 0, 0);
    }

    public SetWindowSizeTask(BlockPos pos, UUID windowId, int width, int height)
    {
        this.pos = pos;
        this.windowId = windowId;
        this.width = width;
        this.height = height;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("windowId", this.windowId);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
//        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
//        this.windowId = nbt.getUniqueId("windowId");
//        this.width = nbt.getInt("width");
//        this.height = nbt.getInt("height");
//
//        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
//        {
//            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
//            LaptopDesktop desktop = laptop.getDesktop();
//            LaptopWindowOld window = desktop.getWindow(this.windowId);
//            if (window != null)
//            {
//                window.syncSetSize(this.width, this.height);
//            }
//            this.setSuccessful();
//        }
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
