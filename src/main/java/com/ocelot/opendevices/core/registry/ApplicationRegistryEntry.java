package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.api.laptop.application.Application;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ApplicationRegistryEntry extends ForgeRegistryEntry<ApplicationRegistryEntry>
{
    private Class<? extends Application> clazz;

    public ApplicationRegistryEntry(Class<? extends Application> clazz)
    {
        this.clazz = clazz;
    }

    public Class<? extends Application> getClazz()
    {
        return clazz;
    }
}