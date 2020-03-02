package com.ocelot.opendevices.core.registry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ComponentBuilderBoardTexture extends ForgeRegistryEntry<ComponentBuilderBoardTexture>
{
    private Item item;
    private ResourceLocation textureLocation;

    public ComponentBuilderBoardTexture(Item item, ResourceLocation textureLocation)
    {
        this.item = item;
        this.textureLocation = textureLocation;
    }

    public Item getItem()
    {
        return item;
    }

    public ResourceLocation getTextureLocation()
    {
        return textureLocation;
    }
}