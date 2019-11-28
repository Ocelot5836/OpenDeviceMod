package com.ocelot.opendevices.api;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.Component;
import com.ocelot.opendevices.api.component.ComponentSerializer;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.StandardComponentSerializer;
import com.ocelot.opendevices.api.laptop.DeviceRegistries;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Objects;

/**
 * <p>Contains components that are used by the base mod content.</p>
 *
 * @author Ocelot
 * @see Component
 */
public class DeviceComponents
{
    @ComponentSerializer.Register(OpenDevices.MOD_ID + ":layout")
    public static final ComponentSerializer<Layout> LAYOUT_SERIALIZER = new StandardComponentSerializer<>(Layout::new);

    private static final HashBiMap<Class<? extends Component>, ResourceLocation> REGISTRY_CACHE = HashBiMap.create();

    private static void fillCache()
    {
        for (Map.Entry<ResourceLocation, ComponentSerializer<?>> entry : DeviceRegistries.COMPONENT_SERIALIZERS.getEntries())
        {
            REGISTRY_CACHE.put(entry.getValue().getComponentClass(), entry.getKey());
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
        componentNBT.putString("registryName", String.valueOf(Objects.requireNonNull(getRegistryName(component.getClass()), "Attempted to serialize unregistered component: \'" + component.getClass().getName() + "\'! Must be registered using ComponentSerializer registry annotation.")));
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
    @SuppressWarnings("unchecked")
    public static <T extends Component> T deserializeComponent(CompoundNBT nbt)
    {
        ResourceLocation registryName = new ResourceLocation(nbt.getString("registryName"));
        CompoundNBT data = nbt.getCompound("data");

        if (!DeviceRegistries.COMPONENT_SERIALIZERS.containsKey(registryName))
            throw new RuntimeException("Attempted to deserialize unregistered component: \'" + registryName + "\'! Must be registered using ComponentSerializer registry annotation.");

        return (T) Objects.requireNonNull(DeviceRegistries.COMPONENT_SERIALIZERS.getValue(registryName)).deserializeNBT(data);
    }

    /**
     * Checks the registry for the matching component class.
     *
     * @param componentClass The class of the component to get the registry name of
     * @return The registry name of the component
     */
    public static ResourceLocation getRegistryName(Class<? extends Component> componentClass)
    {
        if (DeviceRegistries.COMPONENT_SERIALIZERS.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
            fillCache();
        return REGISTRY_CACHE.get(componentClass);
    }
}
