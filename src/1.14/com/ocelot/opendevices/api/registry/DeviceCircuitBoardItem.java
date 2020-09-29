package com.ocelot.opendevices.api.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Specifies an item should have a circuit board texture. Only used for items added to the circuit boards tag.
 *
 * @author Ocelot
 */
public interface DeviceCircuitBoardItem extends DeviceComponentItem
{
    /**
     * Gets the texture location for the specified item.
     *
     * @param item The item to get the texture from
     * @return The texture location
     */
    default ResourceLocation getTextureLocation(Item item)
    {
        return new ResourceLocation(item.getRegistryName().getNamespace(), "board/" + item.getRegistryName().getPath());
    }
}