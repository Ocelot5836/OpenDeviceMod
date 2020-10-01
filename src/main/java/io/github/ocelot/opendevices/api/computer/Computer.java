package io.github.ocelot.opendevices.api.computer;

import io.github.ocelot.opendevices.api.device.Device;

/**
 * <p>A specialized type of {@link Device} for computation and graphics.</p>
 *
 * @author Ocelot
 */
public interface Computer extends Device
{
    int getScreenWidth();

    int getScreenHeight();
}
