package com.ocelot.opendevices.crafting.componentbuilder;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ocelot.opendevices.OpenDevices;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Base64;

public class ComponentBuilderLayout implements INBTSerializable<CompoundNBT>
{
    public static final ResourceLocation EMPTY_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "missing");
    public static final ComponentBuilderLayout EMPTY = new ComponentBuilderLayout(new TranslationTextComponent("component_builder.layout." + OpenDevices.MOD_ID + ".default"), new ResourceLocation(""), ItemStack.EMPTY, 0);

    public static final int SLOT_0 = 0x01;
    public static final int SLOT_1 = 0x02;
    public static final int SLOT_2 = 0x04;
    public static final int SLOT_3 = 0x08;
    public static final int SLOT_4 = 0x10;
    public static final int SLOT_5 = 0x20;
    public static final int SLOT_6 = 0x40;
    public static final int SLOT_7 = 0x80;
    public static final int SLOT_8 = 0x100;

    private ITextComponent title;
    private ResourceLocation textureLocation;
    private ItemStack icon;
    private int slotsUsed;

    @Deprecated
    public ComponentBuilderLayout(ITextComponent title, @Nullable ResourceLocation textureLocation, ItemStack icon, int slotsUsed)
    {
        this.title = title;
        this.textureLocation = textureLocation == null ? null : new ResourceLocation(textureLocation.getNamespace(), "component_builder_layout/" + textureLocation.getPath());
        this.icon = icon;
        this.slotsUsed = slotsUsed;
    }

    public ComponentBuilderLayout(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    public ITextComponent getTitle()
    {
        return title;
    }

    @Nullable
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

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("title", new String(Base64.getEncoder().encode(ITextComponent.Serializer.toJson(this.title).getBytes())));
        if (this.textureLocation != null)
            nbt.putString("textureLocation", String.valueOf(this.textureLocation));
        nbt.put("icon", this.icon.serializeNBT());
        nbt.putInt("slotsUsed", this.slotsUsed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.title = ITextComponent.Serializer.fromJson(new String(Base64.getDecoder().decode(nbt.getString("title"))));
        this.textureLocation = nbt.contains("textureLocation", Constants.NBT.TAG_STRING) ? new ResourceLocation(nbt.getString("textureLocation")) : null;
        this.icon = ItemStack.read(nbt.getCompound("icon"));
        this.slotsUsed = nbt.getInt("slotsUsed");
    }

    public static class Deserializer implements JsonDeserializer<ComponentBuilderLayout>
    {
        @Override
        public ComponentBuilderLayout deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = json.getAsJsonObject();
            ITextComponent title = JSONUtils.deserializeClass(jsonObject, "title", context, ITextComponent.class);
            ItemStack icon = jsonObject.has("icon") ? deserializeIcon(jsonObject.get("icon").getAsJsonObject()) : ItemStack.EMPTY;
            ResourceLocation texture = jsonObject.has("texture") ? new ResourceLocation(JSONUtils.getString(jsonObject, "texture")) : null;
            int slots = parseSlots(jsonObject.get("layout").getAsJsonArray());
            return new ComponentBuilderLayout(title, texture, icon, slots);
        }

        private static ItemStack deserializeIcon(JsonObject object)
        {
            if (!object.has("item"))
            {
                throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
            }
            else
            {
                Item item = JSONUtils.getItem(object, "item");
                if (object.has("data"))
                {
                    throw new JsonParseException("Disallowed data tag found");
                }
                else
                {
                    ItemStack itemstack = new ItemStack(item);
                    if (object.has("nbt"))
                    {
                        try
                        {
                            itemstack.setTag(JsonToNBT.getTagFromJson(JSONUtils.getString(object.get("nbt"), "nbt")));
                        }
                        catch (CommandSyntaxException e)
                        {
                            throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
                        }
                    }

                    return itemstack;
                }
            }
        }

        private static int parseSlots(JsonArray json)
        {
            if (json.size() != 3)
                throw new JsonSyntaxException("3 Strings expected");
            int slotsUsed = 0;
            int i = 0;
            for (JsonElement element : json)
            {
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
                    throw new JsonSyntaxException("Expected String");
                String row = element.getAsString();
                if (row.length() != 3)
                    throw new JsonSyntaxException("String expected length is 3");
                for (int j = 0; j < 3; j++)
                {
                    char character = row.charAt(j);
                    if (character != ' ' && character != '#')
                        throw new JsonSyntaxException("String expected to composed of ' ' or '#'");
                    if (character == '#')
                        slotsUsed |= 1 << i;
                    i++;
                }
            }
            return slotsUsed;
        }
    }
}
