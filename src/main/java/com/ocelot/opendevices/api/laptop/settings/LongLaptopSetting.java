package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Long}. Can be used to read/write any long to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class LongLaptopSetting implements LaptopSetting<Long>
{
    private ResourceLocation registryName;
    private long defaultValue;

    public LongLaptopSetting(ResourceLocation registryName, long defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Long read(CompoundNBT nbt)
    {
        return nbt.getLong(this.registryName.toString());
    }

    @Override
    public void write(Long value, CompoundNBT nbt)
    {
        nbt.putLong(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_LONG);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public Long getDefaultValue()
    {
        return defaultValue;
    }
}
