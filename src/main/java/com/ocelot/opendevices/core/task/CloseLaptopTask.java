package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@Task.Register(OpenDevices.MOD_ID + ":close_laptop")
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
        nbt.putLong("pos", this.pos.toLong());
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity)
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
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
    }
}
