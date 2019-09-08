package com.ocelot.opendevices.api.device;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public interface Device
{
    void save(CompoundNBT nbt);

    void load(CompoundNBT nbt);

    BlockPos getPos();
}
