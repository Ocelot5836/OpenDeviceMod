package com.ocelot.opendevices.api.device;

/**
 * <p>A process is a continuous task that is being executed by a {@link Device}.</p>
 *
 * @author Ocelot
 */
public interface DeviceProcess
{
    /**
     * Updates this process.
     */
    void update();

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
}
