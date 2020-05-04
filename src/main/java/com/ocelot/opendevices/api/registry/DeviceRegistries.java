package com.ocelot.opendevices.api.registry;

import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.settings.LaptopSetting;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.RegistryCache;
import com.ocelot.opendevices.core.registry.*;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;

/**
 * <p>Manages all registries associated with the core device features.</p>
 *
 * @author Ocelot
 */
public class DeviceRegistries
{
    public static IForgeRegistry<LaptopSetting<?>> SETTINGS;
    public static IForgeRegistry<TaskRegistryEntry> TASKS;
    public static IForgeRegistry<DeviceProcessRegistryEntry> PROCESSES;
    public static IForgeRegistry<ApplicationRegistryEntry> APPLICATIONS;
    public static IForgeRegistry<DeviceSerializer<?>> DEVICE_SERIALIZERS;
    @Deprecated
    public static IForgeRegistry<DesktopBackgroundRegistryEntry> DESKTOP_BACKGROUNDS;
    public static IForgeRegistry<TrayItemRegistryEntry> TRAY_ITEMS;

    private static final RegistryCache<TaskRegistryEntry, Class<? extends Task>> TASKS_CACHE;
    private static final RegistryCache<DeviceProcessRegistryEntry, Class<? extends DeviceProcess<?>>> PROCESSES_CACHE;
    private static final RegistryCache<ApplicationRegistryEntry, Class<? extends Application>> APPLICATIONS_CACHE;

    static
    {
        SETTINGS = RegistryManager.ACTIVE.getRegistry(LaptopSetting.class);
        TASKS = RegistryManager.ACTIVE.getRegistry(TaskRegistryEntry.class);
        PROCESSES = RegistryManager.ACTIVE.getRegistry(DeviceProcessRegistryEntry.class);
        APPLICATIONS = RegistryManager.ACTIVE.getRegistry(ApplicationRegistryEntry.class);
        DEVICE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DeviceSerializer.class);
        DESKTOP_BACKGROUNDS = RegistryManager.ACTIVE.getRegistry(DesktopBackgroundRegistryEntry.class);
        TRAY_ITEMS = RegistryManager.ACTIVE.getRegistry(TrayItemRegistryEntry.class);

        TASKS_CACHE = new RegistryCache<>(() -> TASKS.getEntries(), TaskRegistryEntry::getClazz);
        PROCESSES_CACHE = new RegistryCache<>(() -> PROCESSES.getEntries(), DeviceProcessRegistryEntry::getClazz);
        APPLICATIONS_CACHE = new RegistryCache<>(() -> APPLICATIONS.getEntries(), ApplicationRegistryEntry::getClazz);
    }

    private DeviceRegistries() {}

    /**
     * Checks the cached registry values for the specified value.
     *
     * @param value The value to check
     * @return The registry name or null if the value is not registered
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
     * @return The registry name or null if the value is not registered
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
     * @return The registry name or null if the value is not registered
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
     * @return The registry name or null if the value is not registered
     */
    @Nullable
    public static ResourceLocation getApplicationRegistryName(Class<? extends Application> value)
    {
        return APPLICATIONS_CACHE.getRegistryName(value);
    }
}
