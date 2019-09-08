package com.ocelot.opendevices.api.util;

import net.minecraft.inventory.IClearable;
import net.minecraft.item.ItemStack;

public interface ISimpleInventory extends IClearable
{
    int getSizeInventory();

    boolean isEmpty();

    ItemStack getStackInSlot(int slot);

    int getInventoryStackLimit();
}
