package io.github.ocelot.opendevices.api.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Creates new instances of processes for devices.</p>
 *
 * @param <T> The type of device this factory is for
 * @author Ocelot
 */
public interface DeviceProcessSerializer<T extends Device> extends IForgeRegistryEntry<DeviceProcessSerializer<?>>
{
    /**
     * Creates a new process for the specified device.
     *
     * @param device    The device to make a process for
     * @param processId The id the process will be under for the device
     * @return A new process for that device or <code>null</code> if the process could not be made
     */
    @Nullable
    DeviceProcess<T> create(T device, UUID processId);
}
