package com.ocelot.opendevices.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.laptop.Laptop;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;

@TaskManager.Register(OpenDevices.MOD_ID + ":sync_settings")
public class SyncSettingsTask extends Task
{
    private BlockPos pos;
    private CompoundNBT nbt;

    public SyncSettingsTask()
    {
        this(null, new CompoundNBT());
    }

    public SyncSettingsTask(BlockPos pos, CompoundNBT nbt)
    {
        this.pos = pos;
        this.nbt = nbt;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        if (this.pos != null)
        {
            nbt.putLong("pos", this.pos.toLong());
        }
        nbt.put("nbt", this.nbt);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        if (nbt.contains("pos", Constants.NBT.TAG_LONG))
        {
            this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        }
        this.nbt = nbt.getCompound("nbt");

        if (this.pos != null && world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos))).syncSettings(this.nbt);
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
