package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;

import java.util.UUID;

/**
 * <p>A device that can be interacted with by other devices.</p>
 *
 * @author Ocelot
 */
public interface Device
{
    /**
     * @return The serializer to use for this device
     */
    DeviceSerializer<?> getSerializer();

    /**
     * @return The unique id of this device
     */
    UUID getAddress();
}
