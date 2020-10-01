package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.api.device.process.ProcessManager;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>A device that can be interacted with by other devices.</p>
 *
 * @author Ocelot
 */
public interface Device
{
    /**
     * @return The manager for processes if this device can support it
     */
    default Optional<ProcessManager<? extends Device>> getProcessManager()
    {
        return Optional.empty();
    }

    /**
     * @return The world this device is in
     */
    World getDeviceWorld();

    /**
     * @return The serializer to use for this device
     */
    DeviceSerializer<?> getSerializer();

    /**
     * @return The unique id of this device
     */
    UUID getAddress();
}
