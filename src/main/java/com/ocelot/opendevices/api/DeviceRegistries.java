package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.application.Application;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.RegistryCache;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import com.ocelot.opendevices.core.registry.ComponentRegistryEntry;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;

/**
 * <p>Manages all registries associated with the core device features.</p>
 *
 * @author Ocelot
 */
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeviceRegistries
{
    public static IForgeRegistry<LaptopSetting<?>> SETTINGS = null;
    public static IForgeRegistry<TaskRegistryEntry> TASKS = null;
    public static IForgeRegistry<DeviceProcessRegistryEntry> PROCESSES = null;
    public static IForgeRegistry<ApplicationRegistryEntry> APPLICATIONS = null;
    public static IForgeRegistry<ComponentRegistryEntry> COMPONENTS = null;

    private static final RegistryCache<TaskRegistryEntry, Class<? extends Task>> TASKS_CACHE;
    private static final RegistryCache<DeviceProcessRegistryEntry, Class<? extends DeviceProcess<?>>> PROCESSES_CACHE;
    private static final RegistryCache<ApplicationRegistryEntry, Class<? extends Application>> APPLICATIONS_CACHE;

    static
    {
        TASKS_CACHE = new RegistryCache<>(() -> TASKS.getEntries(), TaskRegistryEntry::getClazz);
        PROCESSES_CACHE = new RegistryCache<>(() -> PROCESSES.getEntries(), DeviceProcessRegistryEntry::getClazz);
        APPLICATIONS_CACHE = new RegistryCache<>(() -> APPLICATIONS.getEntries(), ApplicationRegistryEntry::getClazz);
    }

    private DeviceRegistries() {}

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event)
    {
        makeRegistry("settings", LaptopSetting.class).create();
        makeRegistry("tasks", TaskRegistryEntry.class).create();
        makeRegistry("processes", DeviceProcessRegistryEntry.class).create();
        makeRegistry("applications", ApplicationRegistryEntry.class).create();
        makeRegistry("components", ComponentRegistryEntry.class).create();

        SETTINGS = RegistryManager.ACTIVE.getRegistry(LaptopSetting.class);
        TASKS = RegistryManager.ACTIVE.getRegistry(TaskRegistryEntry.class);
        PROCESSES = RegistryManager.ACTIVE.getRegistry(DeviceProcessRegistryEntry.class);
        APPLICATIONS = RegistryManager.ACTIVE.getRegistry(ApplicationRegistryEntry.class);
        COMPONENTS = RegistryManager.ACTIVE.getRegistry(ComponentRegistryEntry.class);
    }

    /**
     * Checks the cached registry values for the specified value.
     *
     * @param value The value to check
     * @return The registry name of null if the value is not registered
     */
    @Nullable
    public static ResourceLocation getTaskRegistryName(Class<? extends Task> value)
    {
        return TASKS_CACHE.getRegistryName(value);
    }

    /**
     * Checks the cached registry values for the specified value.
     *
     * @param value The value to check
     * @return The registry name of null if the value is not registered
     */
    @Nullable
    public static ResourceLocation getProcessRegistryName(Class<? extends DeviceProcess<?>> value)
    {
        return PROCESSES_CACHE.getRegistryName(value);
    }

    /**
     * Checks the cached registry values for the specified value.
     *
     * @param value The value to check
     * @return The registry name of null if the value is not registered
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static ResourceLocation getProcessRegistryName(DeviceProcess<?> value)
    {
        return PROCESSES_CACHE.getRegistryName((Class<? extends DeviceProcess<?>>) value.getClass());
    }

    /**
     * Checks the cached registry values for the specified value.
     *
     * @param value The value to check
     * @return The registry name of null if the value is not registered
     */
    @Nullable
    public static ResourceLocation getApplicationRegistryName(Class<? extends Application> value)
    {
        return APPLICATIONS_CACHE.getRegistryName(value);
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(String name, Class<T> type)
    {
        return new RegistryBuilder<T>().setName(new ResourceLocation(OpenDevices.MOD_ID, name)).setType(type);
    }
}
