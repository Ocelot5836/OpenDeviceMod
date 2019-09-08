package com.ocelot.opendevices.item;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class DeviceBlockItem extends BlockItem
{
    public DeviceBlockItem(Block block)
    {
        super(block, new Item.Properties().maxStackSize(1).group(OpenDevices.TAB));
    }
}
