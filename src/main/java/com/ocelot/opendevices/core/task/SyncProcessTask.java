package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":sync_process")
public class SyncProcessTask extends Task
{
    private BlockPos pos;
    private UUID processId;
    private CompoundNBT data;

    public SyncProcessTask()
    {
        this(null, null, null);
    }

    public SyncProcessTask(BlockPos pos, UUID processId, CompoundNBT data)
    {
        this.pos = pos;
        this.processId = processId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("processId", this.processId);
        nbt.put("data", this.data);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.processId = nbt.getUniqueId("processId");
        this.data = nbt.getCompound("data");

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos))).syncProcess(this.processId, this.data);
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
