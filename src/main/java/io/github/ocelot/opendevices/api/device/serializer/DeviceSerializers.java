package io.github.ocelot.opendevices.api.device.serializer;

import io.github.ocelot.opendevices.core.device.serializer.TileEntityDeviceSerializer;

/**
 * <p>Built-in serializers for device positions.</p>
 *
 * @author Ocelot
 */
public class DeviceSerializers
{
    public static final DeviceSerializer<TileEntityDevice> TILE_ENTITY = new TileEntityDeviceSerializer();
}
