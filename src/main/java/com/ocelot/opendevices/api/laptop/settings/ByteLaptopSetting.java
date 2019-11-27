package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Byte}. Can be used to read/write any byte to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ByteLaptopSetting implements LaptopSetting<Byte>
{
    private ResourceLocation registryName;
    private byte defaultValue;

    public ByteLaptopSetting(ResourceLocation registryName, byte defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Byte read(CompoundNBT nbt)
    {
        return nbt.getByte(this.registryName.toString());
    }

    @Override
    public void write(Byte value, CompoundNBT nbt)
    {
        nbt.putByte(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_BYTE);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public Byte getDefaultValue()
    {
        return defaultValue;
    }
}
