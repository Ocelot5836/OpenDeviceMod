package com.ocelot.opendevices.core.laptop;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LaptopSettings implements INBTSerializable<CompoundNBT>
{
    //TODO add some way to register new settings or smth. For now they will be hardcoded

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }
}