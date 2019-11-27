package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link UUID}. Can be used to read/write any unique ID to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class UUIDLaptopSetting implements LaptopSetting<UUID>
{
    private ResourceLocation registryName;
    private UUID defaultValue;

    public UUIDLaptopSetting(ResourceLocation registryName, UUID defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public UUID read(CompoundNBT nbt)
    {
        return nbt.getUniqueId(this.registryName.toString());
    }

    @Override
    public void write(UUID value, CompoundNBT nbt)
    {
        nbt.putUniqueId(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.hasUniqueId(this.registryName.toString());
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public UUID getDefaultValue()
    {
        return defaultValue;
    }
}
