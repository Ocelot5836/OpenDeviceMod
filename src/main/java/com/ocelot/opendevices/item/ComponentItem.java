package com.ocelot.opendevices.item;

import com.ocelot.opendevices.api.registry.DeviceComponentItem;

public class ComponentItem extends ModItem implements DeviceComponentItem
{
    private int tier;

    public ComponentItem(String registryName, int tier, Properties properties)
    {
        super(registryName, properties);
        this.tier = tier;
    }

    @Override
    public int getTier()
    {
        return tier;
    }
}
