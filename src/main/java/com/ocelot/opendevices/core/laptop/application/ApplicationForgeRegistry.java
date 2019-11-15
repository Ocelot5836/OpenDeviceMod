package com.ocelot.opendevices.core.laptop.application;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ApplicationForgeRegistry extends ForgeRegistryEntry<ApplicationForgeRegistry>
{
    private String className;

    public ApplicationForgeRegistry(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
