package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link String}. Can be used to read/write any string to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class StringLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<String>
{
    private String defaultValue;

    public StringLaptopSetting(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public String read(CompoundNBT nbt)
    {
        return nbt.getString(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(String value, CompoundNBT nbt)
    {
        nbt.putString(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_STRING);
    }

    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }
}
