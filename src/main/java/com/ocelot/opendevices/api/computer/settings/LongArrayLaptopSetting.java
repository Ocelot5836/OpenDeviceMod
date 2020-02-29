package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for an array of the type {@link Long}. Can be used to read/write any long array to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class LongArrayLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<long[]>
{
    private long[] defaultValue;

    public LongArrayLaptopSetting(long... defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public long[] read(CompoundNBT nbt)
    {
        return nbt.getLongArray(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(long[] value, CompoundNBT nbt)
    {
        nbt.putLongArray(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_LONG_ARRAY);
    }

    @Override
    public long[] getDefaultValue()
    {
        return defaultValue;
    }
}
