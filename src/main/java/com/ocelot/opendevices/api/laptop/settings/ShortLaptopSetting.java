package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Short}. Can be used to read/write any short to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ShortLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Short>
{
    private short defaultValue;

    public ShortLaptopSetting(short defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Short read(CompoundNBT nbt)
    {
        return nbt.getShort(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Short value, CompoundNBT nbt)
    {
        nbt.putShort(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_SHORT);
    }

    @Override
    public Short getDefaultValue()
    {
        return defaultValue;
    }
}
