package com.ocelot.opendevices.api.laptop;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

/**
 * <p>Manages all registries associated with the core device features.</p>
 *
 * @author Ocelot
 */
public class DeviceRegistries
{
    public static IForgeRegistry<ApplicationRegistryEntry> APPLICATIONS = null;
    public static IForgeRegistry<LaptopSetting<?>> SETTINGS = null;
    public static IForgeRegistry<TaskRegistryEntry> TASKS = null;

    private DeviceRegistries() {}

    /**
     * This should never be used by the consumer. Core use only!
     */
    @SuppressWarnings("unchecked")
    public static void register()
    {
        makeRegistry("applications", ApplicationRegistryEntry.class).create();
        makeRegistry("settings", LaptopSetting.class).create();
        makeRegistry("tasks", TaskRegistryEntry.class).create();

        APPLICATIONS = RegistryManager.ACTIVE.getRegistry(ApplicationRegistryEntry.class);
        SETTINGS = RegistryManager.ACTIVE.getRegistry(LaptopSetting.class);
        TASKS = RegistryManager.ACTIVE.getRegistry(TaskRegistryEntry.class);
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(String name, Class<T> type)
    {
        return new RegistryBuilder<T>().setName(new ResourceLocation(OpenDevices.MOD_ID, name)).setType(type);
    }
}
