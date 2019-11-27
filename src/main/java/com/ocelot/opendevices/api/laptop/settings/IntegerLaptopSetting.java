package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Integer}. Can be used to read/write any integer to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class IntegerLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Integer>
{
    private int defaultValue;

    public IntegerLaptopSetting(int defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer read(CompoundNBT nbt)
    {
        return nbt.getInt(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Integer value, CompoundNBT nbt)
    {
        nbt.putInt(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_INT);
    }

    @Override
    public Integer getDefaultValue()
    {
        return defaultValue;
    }
}
