package com.ocelot.opendevices.api.component;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.core.registry.ComponentRegistryEntry;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * <p>Contains components that are used by the base mod content.</p>
 *
 * @author Ocelot
 * @see Component
 */
@SuppressWarnings("unchecked")
public class ComponentSerializer
{
    private static final HashBiMap<Class<? extends Component>, ResourceLocation> REGISTRY_CACHE = HashBiMap.create();

    private static void fillCache()
    {
        for (Map.Entry<ResourceLocation, ComponentRegistryEntry> entry : DeviceRegistries.COMPONENTS.getEntries())
        {
            String className = entry.getValue().getComponentClassName();
            try
            {
                Class<?> componentClass = Class.forName(className);

                if (!Component.class.isAssignableFrom(componentClass))
                    throw new IllegalArgumentException("Component: " + componentClass + " does not implement Component. Skipping!");

                REGISTRY_CACHE.put((Class<? extends Component>) componentClass, entry.getKey());
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not bind component class " + className + " for client. Skipping!", e);
            }
        }
    }

    /**
     * Checks the registry for the matching component class.
     *
     * @param componentClass The class of the component to get the registry name of
     * @return The registry name of the component
     */
    public static ResourceLocation isRegistered(Class<? extends Component> componentClass)
    {
        if (DeviceRegistries.COMPONENTS.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
            fillCache();
        return REGISTRY_CACHE.get(componentClass);
    }
}
