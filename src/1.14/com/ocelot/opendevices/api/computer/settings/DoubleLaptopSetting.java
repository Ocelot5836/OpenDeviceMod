package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Double}. Can be used to read/write any double to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class DoubleLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Double>
{
    private double defaultValue;

    public DoubleLaptopSetting(double defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Double read(CompoundNBT nbt)
    {
        return nbt.getDouble(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Double value, CompoundNBT nbt)
    {
        nbt.putDouble(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_DOUBLE);
    }

    @Override
    public Double getDefaultValue()
    {
        return defaultValue;
    }
}
