package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class DeviceTags
{
    public static final Tag<Item> LAPTOPS = makeWrapperTag("devices/laptops");
    public static final Tag<Item> CIRCUIT_BOARDS = makeWrapperTag("circuit_boards");

    private static Tag<Item> makeWrapperTag(String registryName)
    {
        return new ItemTags.Wrapper(new ResourceLocation(OpenDevices.MOD_ID, registryName));
    }
}
