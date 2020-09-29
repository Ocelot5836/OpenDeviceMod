package com.ocelot.opendevices.api.computer.settings;

import com.ocelot.opendevices.api.computer.Computer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>An abstract setting that can be set on the {@link Computer}.</p>
 *
 * @param <T> The type of data the setting handles
 * @author Ocelot
 */
public interface LaptopSetting<T> extends IForgeRegistryEntry<LaptopSetting<?>>
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
     * Checks to see if this setting is on the {@link Computer}.
     *
     * @param nbt The nbt to check
     * @return Whether or not the setting was found on the Laptop.
     */
    boolean contains(CompoundNBT nbt);

    /**
     * @return The default value for the setting
     */
    T getDefaultValue();

    /**
     * Registers a new type of setting for the {@link Computer}.
     *
     * @author Ocelot
     * @see Computer
     */
    @Target(ElementType.FIELD)
    @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>.
         */
        String value();
    }
}
