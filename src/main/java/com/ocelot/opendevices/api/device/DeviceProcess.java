package com.ocelot.opendevices.api.device;

import java.util.UUID;

/**
 * <p>A process is a continuous task that is being executed by a {@link Device}.</p>
 *
 * @author Ocelot
 */
public interface DeviceProcess<T extends Device>
{
    /**
     * Updates this process.
     */
    void update(T device);

    /**
     * Called when this process is terminated by the device.
     *
     * @param forced Whether or not this process self-terminated
     */
    default void onTerminate(boolean forced) {}

    /**
     * @return Whether or not this process has completed execution
     */
    boolean isTerminated();

    /**
     * @return The id of this process
     */
    UUID getProcessId();
}
