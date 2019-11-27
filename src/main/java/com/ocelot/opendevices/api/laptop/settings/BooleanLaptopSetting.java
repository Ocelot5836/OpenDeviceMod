package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Boolean}. Can be used to read/write any boolean to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class BooleanLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Boolean>
{
    private boolean defaultValue;

    public BooleanLaptopSetting(boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Boolean read(CompoundNBT nbt)
    {
        return nbt.getBoolean(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Boolean value, CompoundNBT nbt)
    {
        nbt.putBoolean(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_BYTE);
    }

    @Override
    public Boolean getDefaultValue()
    {
        return defaultValue;
    }
}
