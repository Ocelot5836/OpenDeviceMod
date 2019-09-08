package com.ocelot.opendevices.init;

import net.minecraft.item.Item;
import org.apache.commons.lang3.Validate;

import java.util.HashSet;
import java.util.Set;

public class DeviceItems
{
    private static final Set<Item> ITEMS = new HashSet<>();

    // TODO add items

    public static Item register(Item item)
    {
        Validate.notNull(item.getRegistryName(), "Item %s does not have a registry name", item.getClass());
        ITEMS.add(item);
        return item;
    }

    public static Item[] getItems()
    {
        return ITEMS.toArray(new Item[0]);
    }
}