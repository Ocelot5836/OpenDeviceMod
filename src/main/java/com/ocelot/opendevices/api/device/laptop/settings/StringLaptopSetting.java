package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class StringLaptopSetting implements LaptopSettingFactory<String>
{
    @Override
    public void write(ResourceLocation registryName, String value, CompoundNBT nbt)
    {
        nbt.putString(registryName.toString(), value);
    }

    @Override
    public String read(ResourceLocation registryName, CompoundNBT nbt)
    {
        return nbt.getString(registryName.toString());
    }

    @Override
    public boolean contains(ResourceLocation registryName, CompoundNBT nbt)
    {
        return nbt.contains(registryName.toString(), Constants.NBT.TAG_STRING);
    }
}
