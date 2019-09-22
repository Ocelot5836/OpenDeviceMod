package com.ocelot.opendevices.api.util;

import net.minecraft.inventory.IClearable;
import net.minecraft.item.ItemStack;

/**
 * <p>A simple, clearable Inventory. More bare bones than {@link net.minecraft.inventory.IInventory}</p>
 *
 * @author Ocelot
 */
public interface ISimpleInventory extends IClearable
{
    /**
     * @return The amount of slots in the inventory
     */
    int getSizeInventory();

    /**
     * @return Whether or not the inventory is completely empty
     */
    boolean isEmpty();

    /**
     * Checks the slot provided for a stack.
     *
     * @param slot The slot to check
     * @return The item in the slot or {@link ItemStack#EMPTY} if the slot was out of bounds
     */
    ItemStack getStackInSlot(int slot);

    /**
     * @return The amount of items that can be stored per slot
     */
    int getInventoryStackLimit();
}
