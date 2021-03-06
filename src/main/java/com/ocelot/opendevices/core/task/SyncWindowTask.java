package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

@Deprecated
@Task.Register(OpenDevices.MOD_ID + ":sync_window")
public class SyncWindowTask extends Task
{
    private BlockPos pos;
    private UUID windowId;
    private CompoundNBT contentData;

    public SyncWindowTask()
    {
    }

    public SyncWindowTask(BlockPos pos, UUID windowId, CompoundNBT contentData)
    {
        this.pos = pos;
        this.windowId = windowId;
        this.contentData = contentData;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("windowId", this.windowId);
        nbt.put("contentData", this.contentData);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
//        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
//        this.windowId = nbt.getUniqueId("windowId");
//        this.contentData = nbt.getCompound("contentData");
//
//        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
//        {
//            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
//            LaptopDesktop desktop = laptop.getDesktop();
//
//            LaptopWindowOld window = desktop.getWindow(this.windowId);
//            if (window == null)
//            {
//                OpenDevices.LOGGER.error("Attempted to sync window that doesn't exist!");
//                return;
//            }
//
//            laptop.markDirty();
//            desktop.syncApplication(window, this.contentData);
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
