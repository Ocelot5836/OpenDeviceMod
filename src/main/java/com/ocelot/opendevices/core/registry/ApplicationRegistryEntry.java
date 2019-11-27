package com.ocelot.opendevices.core.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ApplicationRegistryEntry)) return false;
        ApplicationRegistryEntry that = (ApplicationRegistryEntry) o;
        return Objects.equals(this.className, that.className);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.className);
    }

    @Override
    public String toString()
    {
        return "registryName=\'" + this.getRegistryName() + "\', class=\'" + this.className + "\'";
    }
}