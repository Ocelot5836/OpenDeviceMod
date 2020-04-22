package com.ocelot.opendevices.api.crafting;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.lang.reflect.Type;

public class ComponentBuilderLayout extends ForgeRegistryEntry<ComponentBuilderLayout>
{
    public static final int SLOT_0 = 0x01;
    public static final int SLOT_1 = 0x02;
    public static final int SLOT_2 = 0x04;
    public static final int SLOT_3 = 0x08;
    public static final int SLOT_4 = 0x10;
    public static final int SLOT_5 = 0x20;
    public static final int SLOT_6 = 0x40;
    public static final int SLOT_7 = 0x80;
    public static final int SLOT_8 = 0x100;

    private final ResourceLocation textureLocation;
    private final ItemStack icon;
    private final int slotsUsed;

    public ComponentBuilderLayout(ResourceLocation textureLocation, ItemStack icon, int slotsUsed)
    {
        this.textureLocation = textureLocation;
        this.icon = icon;
        this.slotsUsed = slotsUsed;
    }

    public ResourceLocation getTextureLocation()
    {
        return textureLocation;
    }

    public ItemStack getIcon()
    {
        return icon;
    }

    public boolean hasSlot(int slot)
    {
        return (this.slotsUsed & slot) > 0;
    }

    public static class Deserializer implements JsonDeserializer<ComponentBuilderLayout>{
        @Override
        public ComponentBuilderLayout deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {

            return null;
        }
    }
}
