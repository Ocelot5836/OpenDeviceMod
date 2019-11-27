package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link Double}. Can be used to read/write any double to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class DoubleLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<Double>
{
    private ResourceLocation registryName;
    private double defaultValue;

    public DoubleLaptopSetting(ResourceLocation registryName, double defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public Double read(CompoundNBT nbt)
    {
        return nbt.getDouble(this.registryName.toString());
    }

    @Override
    public void write(Double value, CompoundNBT nbt)
    {
        nbt.putDouble(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_DOUBLE);
    }

    @Override
    public Double getDefaultValue()
    {
        return defaultValue;
    }
}
