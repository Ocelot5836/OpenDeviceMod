package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link CompoundNBT}. Can be used to read/write any NBT compound to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class CompoundLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<CompoundNBT>
{
    private CompoundNBT defaultValue;

    public CompoundLaptopSetting(CompoundNBT defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public CompoundNBT read(CompoundNBT nbt)
    {
        return nbt.getCompound(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(CompoundNBT value, CompoundNBT nbt)
    {
        nbt.put(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public CompoundNBT getDefaultValue()
    {
        return defaultValue;
    }
}
