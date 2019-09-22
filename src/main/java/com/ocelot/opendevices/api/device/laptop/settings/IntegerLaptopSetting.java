package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Integer}. Can be used to read/write any integer to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class IntegerLaptopSetting implements LaptopSetting<Integer>
{
    private ResourceLocation registryName;
    private int defaultValue;

    public IntegerLaptopSetting(ResourceLocation registryName, int defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer read(CompoundNBT nbt)
    {
        return nbt.getInt(this.registryName.toString());
    }

    @Override
    public void write(Integer value, CompoundNBT nbt)
    {
        nbt.putInt(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_INT);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public Integer getDefaultValue()
    {
        return defaultValue;
    }
}
