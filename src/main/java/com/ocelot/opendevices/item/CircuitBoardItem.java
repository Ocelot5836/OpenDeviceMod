package com.ocelot.opendevices.item;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.DeviceCircuitBoardItem;
import net.minecraft.item.Item;

public class CircuitBoardItem extends ModItem implements DeviceCircuitBoardItem
{
    private int tier;

    public CircuitBoardItem(String registryName, int tier)
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
