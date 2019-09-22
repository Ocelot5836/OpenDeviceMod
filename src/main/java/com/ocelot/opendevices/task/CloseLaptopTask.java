package com.ocelot.opendevices.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;

@TaskManager.Register(OpenDevices.MOD_ID + ":close_laptop")
public class CloseLaptopTask extends Task
{
    private BlockPos pos;

    public CloseLaptopTask()
    {
        this(null);
    }

    public CloseLaptopTask(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        if (this.pos != null)
        {
            nbt.putLong("pos", this.pos.toLong());
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        if (nbt.contains("pos", Constants.NBT.TAG_LONG))
        {
            this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        }

        if (this.pos != null && world.getTileEntity(this.pos) instanceof LaptopTileEntity)
        {
            ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos))).stopView(player);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
    }

    @Override
    public void processResponse(CompoundNBT nbt)
    {
    }
}
