package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Long}. Can be used to read/write any long to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class LongLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Long>
{
    private long defaultValue;

    public LongLaptopSetting(long defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Long read(CompoundNBT nbt)
    {
        return nbt.getLong(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Long value, CompoundNBT nbt)
    {
        nbt.putLong(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_LONG);
    }

    @Override
    public Long getDefaultValue()
    {
        return defaultValue;
    }
}
