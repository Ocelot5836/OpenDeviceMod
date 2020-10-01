package io.github.ocelot.opendevices.api.computer;

import io.github.ocelot.opendevices.api.device.Device;

/**
 * <p>A specialized type of {@link Device} for computation and graphics.</p>
 *
 * @author Ocelot
 */
public interface Computer extends Device
{
    /**
     * @return The width of the computer display
     */
    int getScreenWidth();

    /**
     * @return The height of the computer display
     */
    int getScreenHeight();
}
