package com.ocelot.opendevices.core.laptop;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LaptopDesktop implements INBTSerializable<CompoundNBT>
{
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
