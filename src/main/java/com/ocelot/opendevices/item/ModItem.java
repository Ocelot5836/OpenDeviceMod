package com.ocelot.opendevices.item;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ModItem extends Item
{
    public ModItem(Properties properties)
    {
        super(properties);
    }

    public ModItem(String registryName, Properties properties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName));
    }
}
