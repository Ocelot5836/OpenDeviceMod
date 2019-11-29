package com.ocelot.opendevices.core.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ComponentRegistryEntry extends ForgeRegistryEntry<ComponentRegistryEntry>
{
    private String className;

    public ComponentRegistryEntry(String className)
    {
        this.className = className;
    }

    public String getComponentClassName()
    {
        return className;
    }

}