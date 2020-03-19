package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.core.devicemanager.DeviceManagerSavedData;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.device.TileEntityDevice;
import com.ocelot.opendevices.core.registry.TileEntityDeviceSerializer;

/**
 * <p>Contains default serializers for {@link Device} that allow them to communicate with {@link DeviceManagerSavedData}.</p>
 *
 * @author Ocelot
 * @see DeviceSerializer
 * @see DeviceManagerSavedData
 */
public class DeviceSerializers
{
    private DeviceSerializers() {}

    @DeviceSerializer.Register(OpenDevices.MOD_ID + ":tile_entity")
    public static final DeviceSerializer<TileEntityDevice> TILE_ENTITY_DEVICE_SERIALIZER = new TileEntityDeviceSerializer();
}
