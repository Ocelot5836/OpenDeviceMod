package io.github.ocelot.opendevices.api;

import io.github.ocelot.opendevices.api.device.process.DeviceProcessSerializer;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

/**
 * <p>Base registries required for core features to function.</p>
 *
 * @author Ocelot
 */
public class DeviceRegistries
{
    public static final IForgeRegistry<DeviceSerializer<?>> DEVICE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DeviceSerializer.class);
    public static final IForgeRegistry<DeviceProcessSerializer<?>> DEVICE_PROCESSES = RegistryManager.ACTIVE.getRegistry(DeviceProcessSerializer.class);
}
