package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Boolean}. Can be used to read/write any integer to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class BooleanLaptopSetting implements LaptopSetting<Boolean>
{
    private ResourceLocation registryName;
    private boolean defaultValue;

    public BooleanLaptopSetting(ResourceLocation registryName, boolean defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Boolean read(CompoundNBT nbt)
    {
        return nbt.getBoolean(this.registryName.toString());
    }

    @Override
    public void write(Boolean value, CompoundNBT nbt)
    {
        nbt.putBoolean(this.registryName.toString(), value);
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
    public Boolean getDefaultValue()
    {
        return defaultValue;
    }
}
