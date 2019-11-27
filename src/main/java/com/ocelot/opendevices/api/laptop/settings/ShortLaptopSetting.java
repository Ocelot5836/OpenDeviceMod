package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Short}. Can be used to read/write any short to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ShortLaptopSetting implements LaptopSetting<Short>
{
    private ResourceLocation registryName;
    private short defaultValue;

    public ShortLaptopSetting(ResourceLocation registryName, short defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Short read(CompoundNBT nbt)
    {
        return nbt.getShort(this.registryName.toString());
    }

    @Override
    public void write(Short value, CompoundNBT nbt)
    {
        nbt.putShort(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_SHORT);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public Short getDefaultValue()
    {
        return defaultValue;
    }
}
