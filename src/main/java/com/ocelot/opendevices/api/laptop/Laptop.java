package com.ocelot.opendevices.api.laptop;

import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.concurrent.Executor;

/**
 * <p>The most abstract form of a Laptop. This contains all the required functionality for the API.</p>
 *
 * @author Ocelot
 */
public interface Laptop extends Executor
{
    /**
     * Writes the specified setting value to NBT.
     *
     * @param setting The setting to set
     * @param value   The new value of the setting
     * @param <T>     The type of setting
     */
    <T> void writeSetting(LaptopSetting<T> setting, T value);

    /**
     * Reads the specified setting value from NBT.
     *
     * @param setting The setting to get
     * @param <T>     The type of setting
     * @return The value of the setting or default if it could not be found
     */
    <T> T readSetting(LaptopSetting<T> setting);

    /**
     * @return The world the laptop is in
     */
    IWorld getWorld();

    /**
     * @return The position of the laptop
     */
    BlockPos getPos();

    /**
     * @return The laptop's desktop
     */
    Desktop getDesktop();
}