package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for an array of the type {@link Byte}. Can be used to read/write any byte to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ByteArrayLaptopSetting implements LaptopSetting<byte[]>
{
    private ResourceLocation registryName;
    private byte[] defaultValue;

    public ByteArrayLaptopSetting(ResourceLocation registryName, byte... defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public byte[] read(CompoundNBT nbt)
    {
        return nbt.getByteArray(this.registryName.toString());
    }

    @Override
    public void write(byte[] value, CompoundNBT nbt)
    {
        nbt.putByteArray(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_BYTE_ARRAY);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public byte[] getDefaultValue()
    {
        return defaultValue;
    }
}
