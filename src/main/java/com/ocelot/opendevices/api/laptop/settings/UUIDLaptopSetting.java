package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.UUID;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link UUID}. Can be used to read/write any unique ID to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class UUIDLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<UUID>
{
    private UUID defaultValue;

    public UUIDLaptopSetting(UUID defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public UUID read(CompoundNBT nbt)
    {
        return nbt.getUniqueId(String.valueOf(this.getRegistryName()));
    }

    @Override
    public void write(UUID value, CompoundNBT nbt)
    {
        nbt.putUniqueId(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.hasUniqueId(String.valueOf(this.getRegistryName()));
    }

    @Override
    public UUID getDefaultValue()
    {
        return defaultValue;
    }
}
