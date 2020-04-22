package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.DeviceComponentItem;
import com.ocelot.opendevices.item.CircuitBoardItem;
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

    public static final Item SOLDER_IRON = register(new ModItem("solder_iron", new Item.Properties().maxDamage(64).group(OpenDevices.TAB)));
    public static final Item SOLDER = register(new ModItem("solder", new Item.Properties().group(OpenDevices.TAB)));

    public static final Item GREEN_CIRCUIT_BOARD = register(new CircuitBoardItem("green_circuit_board", DeviceComponentItem.STANDARD));
    public static final Item BLUE_CIRCUIT_BOARD = register(new CircuitBoardItem("blue_circuit_board", DeviceComponentItem.IMPROVED));
    public static final Item RED_CIRCUIT_BOARD = register(new CircuitBoardItem("red_circuit_board", DeviceComponentItem.ULTIMATE));

    public static final Item GREEN_COMPONENT_MOTHERBOARD = register(new ComponentItem("green_motherboard", DeviceComponentItem.STANDARD));
    public static final Item BLUE_COMPONENT_MOTHERBOARD = register(new ComponentItem("blue_motherboard", DeviceComponentItem.IMPROVED));
    public static final Item RED_COMPONENT_MOTHERBOARD = register(new ComponentItem("red_motherboard", DeviceComponentItem.ULTIMATE));
    public static final Item COMPONENT_CPU = register(new ComponentItem("cpu", DeviceComponentItem.NONE));
    public static final Item COMPONENT_RAM = register(new ComponentItem("ram", DeviceComponentItem.NONE));
    public static final Item COMPONENT_GPU = register(new ComponentItem("gpu", DeviceComponentItem.NONE));

    public static final Item FLASH_CHIP = register(new ComponentItem("flash_chip", DeviceComponentItem.NONE));
    public static final Item CONTROLLER_CHIP = register(new ComponentItem("controller_chip", DeviceComponentItem.NONE));

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