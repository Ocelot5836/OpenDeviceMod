package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.container.ComponentBuilderContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class DeviceContainers
{
    private static final Set<ContainerType<?>> CONTAINER_TYPES = new HashSet<>();

    public static final ContainerType<ComponentBuilderContainer> COMPONENT_BUILDER = register("component_builder", ComponentBuilderContainer::new);

    public static <T extends Container> ContainerType<T> register(String registryName, ContainerType.IFactory<T> factory)
    {
        ContainerType<T> containerType = new ContainerType<>(factory);
        CONTAINER_TYPES.add(containerType.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName)));
        return containerType;
    }

    public static ContainerType<?>[] getContainerTypes()
    {
        return CONTAINER_TYPES.toArray(new ContainerType[0]);
    }

}
