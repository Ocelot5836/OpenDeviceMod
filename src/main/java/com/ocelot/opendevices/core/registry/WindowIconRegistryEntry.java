package com.ocelot.opendevices.core.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class WindowIconRegistryEntry extends ForgeRegistryEntry<WindowIconRegistryEntry>
{
    private ResourceLocation location;

    public WindowIconRegistryEntry(ResourceLocation location)
    {
        this.location = location;
    }

    public ResourceLocation getLocation()
    {
        return location;
    }
}