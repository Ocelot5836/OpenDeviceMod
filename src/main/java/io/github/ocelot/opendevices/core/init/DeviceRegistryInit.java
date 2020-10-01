package io.github.ocelot.opendevices.core.init;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.device.process.DeviceProcessSerializer;
import io.github.ocelot.opendevices.api.device.process.DeviceProcesses;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeviceRegistryInit
{
    @SubscribeEvent
    public static void registerDeviceSerializers(RegistryEvent.Register<DeviceSerializer<?>> event)
    {
        IForgeRegistry<DeviceSerializer<?>> registry = event.getRegistry();
        registry.register(DeviceSerializers.TILE_ENTITY);
    }

    @SubscribeEvent
    public static void registerDeviceProcesses(RegistryEvent.Register<DeviceProcessSerializer<?>> event)
    {
        IForgeRegistry<DeviceProcessSerializer<?>> registry = event.getRegistry();
        registry.register(DeviceProcesses.TEST);
    }

    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event)
    {
        new RegistryBuilder<>().setName(new ResourceLocation(OpenDevices.MOD_ID, "device_serializers")).setType(c(DeviceSerializer.class)).disableSync().create();
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> c(Class<?> cls)
    {
        return (Class<T>) cls;
    }
}
