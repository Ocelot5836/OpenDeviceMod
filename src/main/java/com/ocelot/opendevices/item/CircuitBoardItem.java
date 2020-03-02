package com.ocelot.opendevices.item;

import com.ocelot.opendevices.api.registry.DeviceCircuitBoardItem;

public class CircuitBoardItem extends ModItem implements DeviceCircuitBoardItem
{
    private int tier;

    public CircuitBoardItem(String registryName, int tier, Properties properties)
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
