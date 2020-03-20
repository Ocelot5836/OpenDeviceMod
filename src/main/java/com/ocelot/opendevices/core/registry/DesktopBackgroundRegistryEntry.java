package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.api.computer.desktop.DesktopBackground;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DesktopBackgroundRegistryEntry extends ForgeRegistryEntry<DesktopBackgroundRegistryEntry>
{
    private Class<? extends DesktopBackground> clazz;

    public DesktopBackgroundRegistryEntry(Class<? extends DesktopBackground> clazz)
    {
        this.clazz = clazz;
    }

    public Class<? extends DesktopBackground> getClazz()
    {
        return clazz;
    }
}