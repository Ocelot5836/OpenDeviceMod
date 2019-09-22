package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class ResourceLocationLaptopSetting implements LaptopSetting<ResourceLocation>
{
    private ResourceLocation registryName;
    private ResourceLocation defaultValue;

    public ResourceLocationLaptopSetting(ResourceLocation registryName, ResourceLocation defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public ResourceLocation read(CompoundNBT nbt)
    {
        return new ResourceLocation(nbt.getString(this.registryName.toString()));
    }

    @Override
    public void write(ResourceLocation value, CompoundNBT nbt)
    {
        nbt.putString(this.registryName.toString(), value.toString());
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_STRING);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public ResourceLocation getDefaultValue()
    {
        return defaultValue;
    }
}
