package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Function;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link INBTSerializable<CompoundNBT>}. Can be used to read/write any serializable NBT to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class SerializableLaptopSetting<T extends INBTSerializable<CompoundNBT>> implements LaptopSetting<T>
{
    private ResourceLocation registryName;
    private Function<CompoundNBT, T> factory;
    private T defaultValue;

    public SerializableLaptopSetting(ResourceLocation registryName, Function<CompoundNBT, T> factory, T defaultValue)
    {
        this.registryName = registryName;
        this.factory = factory;
        this.defaultValue = defaultValue;
    }

    @Override
    public T read(CompoundNBT nbt)
    {
        return this.factory.apply(nbt.getCompound(this.registryName.toString()));
    }

    @Override
    public void write(T value, CompoundNBT nbt)
    {
        nbt.put(this.registryName.toString(), value.serializeNBT());
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
    public T getDefaultValue()
    {
        return defaultValue;
    }
}
