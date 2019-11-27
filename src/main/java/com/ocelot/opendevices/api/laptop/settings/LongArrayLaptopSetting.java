package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for an array of the type {@link Long}. Can be used to read/write any long array to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class LongArrayLaptopSetting implements LaptopSetting<long[]>
{
    private ResourceLocation registryName;
    private long[] defaultValue;

    public LongArrayLaptopSetting(ResourceLocation registryName, long... defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public long[] read(CompoundNBT nbt)
    {
        return nbt.getLongArray(this.registryName.toString());
    }

    @Override
    public void write(long[] value, CompoundNBT nbt)
    {
        nbt.putLongArray(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_LONG_ARRAY);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public long[] getDefaultValue()
    {
        return defaultValue;
    }
}
