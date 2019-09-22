package com.ocelot.opendevices.api.device.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>An abstract setting that can be set on the {@link com.ocelot.opendevices.api.device.laptop.Laptop}.</>
 *
 * @param <T> The type of data the setting handles
 * @author Ocelot
 * @see SettingsManager
 */
public interface LaptopSetting<T>
{
    /**
     * Reads this setting from NBT.
     *
     * @param nbt The nbt to read the data from
     * @return The value or null if it could not be found
     */
    @Nullable
    T read(CompoundNBT nbt);

    /**
     * Writes the specified value to NBT.
     *
     * @param value The value to write
     * @param nbt   The nbt to write the data to
     */
    void write(T value, CompoundNBT nbt);

    /**
     * Checks to see if this setting is on the {@link com.ocelot.opendevices.api.device.laptop.Laptop}.
     *
     * @param nbt The nbt to check
     * @return Whether or not the setting was found on the Laptop.
     */
    boolean contains(CompoundNBT nbt);

    /**
     * @return The registry name of the setting
     */
    ResourceLocation getRegistryName();

    /**
     * @return The default value for the setting
     */
    T getDefaultValue();
}
