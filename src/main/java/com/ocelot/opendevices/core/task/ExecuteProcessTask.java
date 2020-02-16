package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":execute_process")
public class ExecuteProcessTask extends Task
{
    private BlockPos pos;
    private ResourceLocation processName;
    private UUID processId;
    private CompoundNBT data;

    public ExecuteProcessTask()
    {
        this(null, null, null, null);
    }

    public ExecuteProcessTask(BlockPos pos, ResourceLocation processName, UUID processId, CompoundNBT data)
    {
        this.pos = pos;
        this.processName = processName;
        this.processId = processId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putString("processName", this.processName.toString());
        nbt.putUniqueId("processId", this.processId);
        if (this.data != null)
        {
            nbt.put("data", this.data);
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.processName = new ResourceLocation(nbt.getString("processName"));
        this.processId = nbt.getUniqueId("processId");
        this.data = nbt.contains("data", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("data") : null;

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            if (laptop.syncExecuteProcess(this.processName, this.processId, !world.isRemote(), false))
            {
                this.setSuccessful();
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
            this.pos = BlockPos.fromLong(nbt.getLong("pos"));
            this.processName = new ResourceLocation(nbt.getString("processName"));
            this.processId = nbt.getUniqueId("processId");
            this.data = nbt.contains("data", Constants.NBT.TAG_COMPOUND) ? nbt.getCompound("data") : null;

            if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
            {
                LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
                laptop.syncExecuteProcess(this.processName, this.processId, false, true);
                if (this.data != null)
                {
                    laptop.syncProcess(this.processId, this.data);
                    laptop.syncProcess(this.processId);
                }
            }
        }
    }
}
