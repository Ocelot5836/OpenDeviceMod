package io.github.ocelot.opendevices.api.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.core.device.process.TestDeviceProcess;

/**
 * <p>Built-in processes provided by the base mod.</p>
 *
 * @author Ocelot
 */
public final class DeviceProcesses
{
    public static final DeviceProcessSerializer<Device> TEST = new BasicProcessSerializer<>(TestDeviceProcess::new);

    private DeviceProcesses()
    {
    }
}
