package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.item.ComponentItem;
import com.ocelot.opendevices.item.ModItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeviceItems
{
    private static final Set<Item> ITEMS = new HashSet<>();
    private static final Map<DeviceMaterials, Item> MATERIALS = new HashMap<>();

    static
    {
        for (DeviceMaterials material : DeviceMaterials.values())
        {
            Item materialItem = new ModItem(material.getRegistryName(), new Item.Properties().group(material.getGroup()));
            MATERIALS.put(material, materialItem);
            register(materialItem);
        }
    }

    public static final Item SOLDER_IRON = register(new ModItem("solder_iron", new Item.Properties().maxDamage(500).group(OpenDevices.TAB)));
    public static final Item SOLDER = register(new ModItem("solder", new Item.Properties().group(OpenDevices.TAB)));
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

    public static Item getMaterialItem(DeviceMaterials material)
    {
        return MATERIALS.get(material);
    }
}