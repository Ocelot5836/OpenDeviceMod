package com.ocelot.opendevices.core.computer.taskbar;

import com.ocelot.opendevices.api.computer.taskbar.TrayItem;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class LaptopTrayItem implements TrayItem
{
    private UUID id;
    private ResourceLocation registryName;

    public LaptopTrayItem(UUID id, ResourceLocation registryName)
    {
        this.id = id;
        this.registryName = registryName;
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
