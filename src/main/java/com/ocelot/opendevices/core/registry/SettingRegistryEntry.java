package com.ocelot.opendevices.core.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class SettingRegistryEntry extends ForgeRegistryEntry<SettingRegistryEntry>
{
    @Override
    public String toString()
    {
        return "registryName=\'" + this.getRegistryName() + "\'";
    }
}