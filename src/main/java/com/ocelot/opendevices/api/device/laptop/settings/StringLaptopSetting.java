package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link String}. Can be used to read/write any string to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class StringLaptopSetting implements LaptopSetting<String>
{
    private ResourceLocation registryName;
    private String defaultValue;

    public StringLaptopSetting(ResourceLocation registryName, String defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public String read(CompoundNBT nbt)
    {
        return nbt.getString(this.registryName.toString());
    }

    @Override
    public void write(String value, CompoundNBT nbt)
    {
        nbt.putString(this.registryName.toString(), value);
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
    public String getDefaultValue()
    {
        return defaultValue;
    }
}
