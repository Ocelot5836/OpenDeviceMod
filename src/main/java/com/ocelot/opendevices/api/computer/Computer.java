package com.ocelot.opendevices.api.computer;

import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.computer.desktop.Desktop;
import com.ocelot.opendevices.api.computer.settings.LaptopSetting;
import com.ocelot.opendevices.api.computer.window.WindowManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>The most abstract form of a Laptop. This contains all the required functionality for the API.</p>
 *
 * @author Ocelot
 */
public interface Computer extends Device
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
     * TODO remove once a device manager is added to find devices by address
     * @return The user currently using the laptop or null if no player is using the laptop
     */
    @Nullable
    PlayerEntity getUser();

    /**
     * @return The position of the laptop
     */
    BlockPos getPos();

    /**
     * @return The laptop's desktop
     */
    Desktop getDesktop();

    /**
     * @return The laptop's window manager
     */
    WindowManager getWindowManager();

    /**
     * @return The laptop's task bar
     */
    TaskBar getTaskBar();

    @Nullable
    DeviceProcess<Computer> getProcess(UUID id);
}
