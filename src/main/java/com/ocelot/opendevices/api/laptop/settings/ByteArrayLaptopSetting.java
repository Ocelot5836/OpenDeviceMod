package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for an array of the type {@link Byte}. Can be used to read/write any byte to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ByteArrayLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<byte[]>
{
    private byte[] defaultValue;

    public ByteArrayLaptopSetting(byte... defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public byte[] read(CompoundNBT nbt)
    {
        return nbt.getByteArray(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(byte[] value, CompoundNBT nbt)
    {
        nbt.putByteArray(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_BYTE_ARRAY);
    }

    @Override
    public byte[] getDefaultValue()
    {
        return defaultValue;
    }
}
