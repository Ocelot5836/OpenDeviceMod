package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

@Deprecated
@Task.Register(OpenDevices.MOD_ID + ":focus_window")
public class FocusWindowTask extends Task
{
    private BlockPos pos;
    private UUID windowId;

    public FocusWindowTask()
    {
        this(null, null);
    }

    public FocusWindowTask(BlockPos pos, @Nullable UUID windowId)
    {
        this.pos = pos;
        this.windowId = windowId;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        if (this.windowId != null)
        {
            nbt.putUniqueId("windowId", this.windowId);
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
//        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
//        this.windowId = nbt.hasUniqueId("windowId") ? nbt.getUniqueId("windowId") : null;
//
//        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
//        {
//            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
//            laptop.getDesktop().syncFocusWindow(this.windowId);
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
