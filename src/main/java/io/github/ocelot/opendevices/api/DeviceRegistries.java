package io.github.ocelot.opendevices.api;

import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class DeviceRegistries
{
    public static final IForgeRegistry<DeviceSerializer<?>> DEVICE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DeviceSerializer.class);
}
