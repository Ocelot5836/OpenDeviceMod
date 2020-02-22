package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":update_laptop_user")
public class UpdateLaptopUserTask extends Task
{
    private BlockPos pos;
    private UUID user;

    public UpdateLaptopUserTask()
    {
        this(null, null);
    }

    public UpdateLaptopUserTask(BlockPos pos, UUID user)
    {
        this.pos = pos;
        this.user = user;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putLong("pos", this.pos.toLong());
        nbt.putUniqueId("userId", this.user);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.user = nbt.getUniqueId("userId");

        if (world.getTileEntity(this.pos) instanceof LaptopTileEntity && world.getPlayerByUuid(this.user) != null)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(this.pos));
            if (laptop.view(world.getPlayerByUuid(this.user)))
            {
                this.setSuccessful();
            }
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
