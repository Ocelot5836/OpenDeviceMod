package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Float}. Can be used to read/write any float to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class FloatLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Float>
{
    private ResourceLocation registryName;
    private float defaultValue;

    public FloatLaptopSetting(ResourceLocation registryName, float defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Float read(CompoundNBT nbt)
    {
        return nbt.getFloat(this.registryName.toString());
    }

    @Override
    public void write(Float value, CompoundNBT nbt)
    {
        nbt.putFloat(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_FLOAT);
    }

    @Override
    public Float getDefaultValue()
    {
        return defaultValue;
    }
}
