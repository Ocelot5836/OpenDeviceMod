package com.ocelot.opendevices.core.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ApplicationRegistryEntry extends ForgeRegistryEntry<ApplicationRegistryEntry>
{
    private String className;

    public ApplicationRegistryEntry(String className)
    {
        this.className = className;
    }

    public String getApplicationClassName()
    {
        return className;
    }

}