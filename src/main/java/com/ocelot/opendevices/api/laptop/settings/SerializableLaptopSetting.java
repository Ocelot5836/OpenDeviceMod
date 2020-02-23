package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Function;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link INBTSerializable}. Can be used to read/write any serializable NBT to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class SerializableLaptopSetting<T extends INBTSerializable<CompoundNBT>> extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<T>
{
    private Function<CompoundNBT, T> factory;
    private T defaultValue;

    public SerializableLaptopSetting(Function<CompoundNBT, T> factory, T defaultValue)
    {
        this.factory = factory;
        this.defaultValue = defaultValue;
    }

    @Override
    public T read(CompoundNBT nbt)
    {
        return this.factory.apply(nbt.getCompound(String.valueOf(this.getRegistryName())));
    }

    @Override
    public void write(T value, CompoundNBT nbt)
    {
        nbt.put(String.valueOf(this.getRegistryName()), value.serializeNBT());
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public T getDefaultValue()
    {
        return defaultValue;
    }
}
