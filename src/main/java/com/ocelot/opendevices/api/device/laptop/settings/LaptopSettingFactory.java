package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface LaptopSettingFactory<T>
{
    void write(ResourceLocation registryName, T value, CompoundNBT nbt);

    T read(ResourceLocation registryName, CompoundNBT nbt);

    boolean contains(ResourceLocation registryName, CompoundNBT nbt);
}
