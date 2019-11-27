package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link CompoundNBT}. Can be used to read/write any NBT compound to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class CompoundLaptopSetting implements LaptopSetting<CompoundNBT>
{
    private ResourceLocation registryName;
    private CompoundNBT defaultValue;

    public CompoundLaptopSetting(ResourceLocation registryName, CompoundNBT defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public CompoundNBT read(CompoundNBT nbt)
    {
        return nbt.getCompound(this.registryName.toString());
    }

    @Override
    public void write(CompoundNBT value, CompoundNBT nbt)
    {
        nbt.put(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public CompoundNBT getDefaultValue()
    {
        return defaultValue;
    }
}
