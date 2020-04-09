package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.api.computer.Computer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Function;

public class TrayItemRegistryEntry extends ForgeRegistryEntry<TrayItemRegistryEntry>
{
    private Function<Computer, Boolean> clickListener;

    public TrayItemRegistryEntry(Function<Computer, Boolean> clickListener)
    {
        this.clickListener = clickListener;
    }

    public Function<Computer, Boolean> getClickListener()
    {
        return clickListener;
    }
}