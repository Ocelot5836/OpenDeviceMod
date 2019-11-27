package com.ocelot.opendevices.api.laptop;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import com.ocelot.opendevices.core.registry.SettingRegistryEntry;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * <p>Manages all registries associated with the core device features.</p>
 *
 * @author Ocelot
 */
public class DeviceRegistries
{
    public static IForgeRegistry<ApplicationRegistryEntry> APPLICATIONS = null;
    public static IForgeRegistry<SettingRegistryEntry> SETTINGS = null;
    public static IForgeRegistry<TaskRegistryEntry> TASKS = null;

    private DeviceRegistries() {}

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void register()
    {
        APPLICATIONS = new RegistryBuilder<ApplicationRegistryEntry>().setName(new ResourceLocation(OpenDevices.MOD_ID, "applications")).setType(ApplicationRegistryEntry.class).create();
        SETTINGS = new RegistryBuilder<SettingRegistryEntry>().setName(new ResourceLocation(OpenDevices.MOD_ID, "settings")).setType(SettingRegistryEntry.class).create();
        TASKS = new RegistryBuilder<TaskRegistryEntry>().setName(new ResourceLocation(OpenDevices.MOD_ID, "tasks")).setType(TaskRegistryEntry.class).create();
    }
}
