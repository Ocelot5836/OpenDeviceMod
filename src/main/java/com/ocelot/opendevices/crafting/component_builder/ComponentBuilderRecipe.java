package com.ocelot.opendevices.crafting.component_builder;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.init.DeviceRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;

public class ComponentBuilderRecipe implements IRecipe<IInventory>, IShapedRecipe<IInventory>
{
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final ComponentBuilderBoardLayout layout;
    private final ResourceLocation id;
    private final String group;

    public ComponentBuilderRecipe(NonNullList<Ingredient> recipeItems, ItemStack recipeOutput, ComponentBuilderBoardLayout layout, ResourceLocation id, String group)
    {
        this.recipeItems = recipeItems;
        this.recipeOutput = recipeOutput;
        this.layout = layout;
        this.id = id;
        this.group = group;
    }

    public ComponentBuilderBoardLayout getLayout()
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

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return recipeItems;
    }

    public boolean matches(IInventory inventory, World world)
    {
        return this.checkMatch(inventory, true) || this.checkMatch(inventory, false);
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(IInventory craftingInventory, boolean reverse)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                Ingredient ingredient;
                if (reverse)
                {
                    ingredient = this.recipeItems.get(3 - i - 1 + j * 3);
                }
                else
                {
                    ingredient = this.recipeItems.get(i + j * 3);
                }

                if (!ingredient.test(craftingInventory.getStackInSlot(i + j * 3)))
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
        public ComponentBuilderRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            return null;
        }

        @Nullable
        @Override
        public ComponentBuilderRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            return null;
        }

        @Override
        public void write(PacketBuffer buffer, ComponentBuilderRecipe recipe)
        {

        }
    }
}
