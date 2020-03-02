package com.ocelot.opendevices.item;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.DeviceComponentItem;
import net.minecraft.item.Item;

public class ComponentItem extends ModItem implements DeviceComponentItem
{
    private int tier;

    public ComponentItem(String registryName, int tier)
    {
        super(registryName, new Item.Properties().group(OpenDevices.TAB));
        this.tier = tier;
    }

    @Override
    public int getTier()
    {
        return tier;
    }
}
