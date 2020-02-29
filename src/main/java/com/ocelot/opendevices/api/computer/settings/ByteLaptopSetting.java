package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Byte}. Can be used to read/write any byte to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ByteLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Byte>
{
    private byte defaultValue;

    public ByteLaptopSetting(byte defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Byte read(CompoundNBT nbt)
    {
        return nbt.getByte(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(Byte value, CompoundNBT nbt)
    {
        nbt.putByte(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_BYTE);
    }

    @Override
    public Byte getDefaultValue()
    {
        return defaultValue;
    }
}
