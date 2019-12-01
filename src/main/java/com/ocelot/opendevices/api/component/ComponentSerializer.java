package com.ocelot.opendevices.api.component;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.core.registry.ComponentRegistryEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

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
     * Serializes the specified component into an NBT tag.
     *
     * @param component The component to serialize
     * @return The component serialized into NBT
     */
    public static CompoundNBT serializeComponent(Component component)
    {
        CompoundNBT componentNBT = new CompoundNBT();
        componentNBT.putString("registryName", String.valueOf(Objects.requireNonNull(getRegistryName(component.getClass()), "Attempted to serialize unregistered component: '" + component.getClass().getName() + "'! Must be registered using Component#Register annotation.")));
        componentNBT.put("data", component.serializeNBT());
        return componentNBT;
    }

    /**
     * Deserialized the specified NBT tag into a component.
     *
     * @param nbt The component serialized into NBT
     * @param <T> The type of component to deserialize into
     * @return The component deserialized from NBT
     */
    public static <T extends Component> T deserializeComponent(CompoundNBT nbt)
    {
        ResourceLocation registryName = new ResourceLocation(nbt.getString("registryName"));
        CompoundNBT data = nbt.getCompound("data");

        if (!DeviceRegistries.COMPONENTS.containsKey(registryName))
            throw new RuntimeException("Attempted to deserialize unregistered component: '" + registryName + "'! Must be registered using ComponentSerializer registry annotation.");

        T component = createComponent(registryName, data);
        if (component == null)
            throw new RuntimeException("Component could not create itself due to an error: " + registryName);

        return component;
    }

    /**
     * Creates a new component based on registry name.
     *
     * @param registryName The registry component of the app to make
     * @return The component created or null if there was an error
     */
    @Nullable
    public static <T extends Component> T createComponent(ResourceLocation registryName, CompoundNBT nbt)
    {
        if (!DeviceRegistries.COMPONENTS.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Component: " + registryName + ". Use Component#Register annotation to register a component.");
        }

        try
        {
            return (T) getComponentClass(registryName).getConstructor(CompoundNBT.class).newInstance(nbt);
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create component: " + registryName + ". Verify there is a public empty constructor.", e);
        }

        return null;
    }

    /**
     * Checks the registry for the matching component class.
     *
     * @param componentClass The class of the component to get the registry name of
     * @return The registry name of the component
     */
    public static ResourceLocation getRegistryName(Class<? extends Component> componentClass)
    {
        if (DeviceRegistries.COMPONENTS.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
            fillCache();
        return REGISTRY_CACHE.get(componentClass);
    }

    /**
     * Checks the registry for a class under the specified registry name.
     *
     * @param registryName The registry name of the component to get
     * @return The physical class of that component
     */
    public static <T extends Component> Class<T> getComponentClass(ResourceLocation registryName)
    {
        if (!DeviceRegistries.COMPONENTS.containsKey(registryName))
            throw new RuntimeException("Unregistered Component: " + registryName + ". Use Component#Register annotations to register a component.");

        if (REGISTRY_CACHE.isEmpty())
            fillCache();
        return (Class<T>) REGISTRY_CACHE.inverse().get(registryName);
    }
}
