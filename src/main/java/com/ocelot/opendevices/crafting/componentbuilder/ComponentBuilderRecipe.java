package com.ocelot.opendevices.crafting.componentbuilder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ocelot.opendevices.init.DeviceRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ComponentBuilderRecipe implements IRecipe<IInventory>, IShapedRecipe<IInventory>
{
    private final NonNullList<Ingredient> recipeItems;
    private final Ingredient recipeInput;
    private final ItemStack recipeOutput;
    private final int solderAmount;
    private final ResourceLocation layout;
    private final ResourceLocation id;
    private final String group;

    public ComponentBuilderRecipe(ResourceLocation id, String group, NonNullList<Ingredient> recipeItems, Ingredient recipeInput, ItemStack recipeOutput, int solderAmount, ResourceLocation layout)
    {
        this.recipeItems = recipeItems;
        this.recipeInput = recipeInput;
        this.recipeOutput = recipeOutput;
        this.solderAmount = solderAmount;
        this.layout = layout;
        this.id = id;
        this.group = group;
    }

    public ResourceLocation getLayout()
    {
        return layout;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return DeviceRecipes.COMPONENT_BUILDER_SERIALIZER;
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return recipeOutput;
    }

    public Ingredient getRecipeInput()
    {
        return recipeInput;
    }

    public int getSolderAmount()
    {
        return solderAmount;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return recipeItems;
    }

    public boolean matches(IInventory inventory, World world)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                int index = i + j * 3;
                if (!this.recipeItems.get(index).test(inventory.getStackInSlot(index)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inventory)
    {
        return this.getRecipeOutput().copy();
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width >= 3 && height >= 3;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return DeviceRecipes.COMPONENT_BUILDER;
    }

    @Override
    public int getRecipeWidth()
    {
        return 3;
    }

    @Override
    public int getRecipeHeight()
    {
        return 3;
    }

    private static NonNullList<Ingredient> deserializeIngredients(ResourceLocation layout, String[] pattern, Map<String, Ingredient> keys)
    {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(3 * 3, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i)
        {
            for (int j = 0; j < pattern[i].length(); ++j)
            {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null)
                {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + i * 3, ingredient);
            }
        }

        if (!set.isEmpty())
        {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        else
        {
            return nonnulllist;
        }
    }

    private static String[] patternFromJson(JsonArray jsonArr)
    {
        String[] astring = new String[jsonArr.size()];
        if (astring.length != 3)
        {
            throw new JsonSyntaxException("Invalid pattern: 3 rows are required");
        }
        else
        {
            for (int i = 0; i < astring.length; ++i)
            {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() != 3)
                {
                    throw new JsonSyntaxException("Invalid pattern: 3 columns are required");
                }

                if (i > 0 && astring[0].length() != s.length())
                {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    private static Map<String, Ingredient> deserializeKey(JsonObject json)
    {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet())
        {
            if (entry.getKey().length() != 1)
            {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey()))
            {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ComponentBuilderRecipe>
    {
        @Override
        public ComponentBuilderRecipe read(ResourceLocation id, JsonObject json)
        {
            ResourceLocation layout = new ResourceLocation(JSONUtils.getString(json, "layout"));
            String group = JSONUtils.getString(json, "group", "");
            int solderAmount = MathHelper.clamp(JSONUtils.getInt(json, "solderAmount", 1), 1, 64);
            Map<String, Ingredient> map = ComponentBuilderRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] ingredientsString = ComponentBuilderRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern"));
            NonNullList<Ingredient> ingredients = ComponentBuilderRecipe.deserializeIngredients(layout, ingredientsString, map);
            Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ComponentBuilderRecipe(id, group, ingredients, input, result, solderAmount, layout);
        }

        @Nullable
        @Override
        public ComponentBuilderRecipe read(ResourceLocation id, PacketBuffer buffer)
        {
            ResourceLocation layout = buffer.readResourceLocation();
            String s = buffer.readString(32767);
            int solderAmount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int k = 0; k < ingredients.size(); ++k)
            {
                ingredients.set(k, Ingredient.read(buffer));
            }

            Ingredient input = Ingredient.read(buffer);
            ItemStack result = buffer.readItemStack();
            return new ComponentBuilderRecipe(id, s, ingredients, input, result, solderAmount, layout);
        }

        @Override
        public void write(PacketBuffer buffer, ComponentBuilderRecipe recipe)
        {
            buffer.writeResourceLocation(recipe.layout);
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.solderAmount);

            for (Ingredient ingredient : recipe.recipeItems)
            {
                ingredient.write(buffer);
            }

            recipe.recipeInput.write(buffer);
            buffer.writeItemStack(recipe.recipeOutput);
        }
    }
}
