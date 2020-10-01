package io.github.ocelot.opendevices.api.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.UUID;
import java.util.function.Function;

/**
 * <p>A simple implementation of {@link DeviceProcessSerializer}.</p>
 *
 * @param <T> The type of device this factory is for
 * @author Ocelot
 */
public class BasicProcessSerializer<T extends Device> extends ForgeRegistryEntry<DeviceProcessSerializer<?>> implements DeviceProcessSerializer<T>
{
    private final Function<T, DeviceProcess<T>> generator;

    public BasicProcessSerializer(Function<T, DeviceProcess<T>> generator)
    {
        this.generator = generator;
    }

    @Override
    public DeviceProcess<T> create(T device, UUID processId)
    {
        return this.generator.apply(device);
    }
}
