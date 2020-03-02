package com.ocelot.opendevices.crafting.component_builder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.init.DeviceRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ComponentBuilderRecipe implements IRecipe<IInventory>, IShapedRecipe<IInventory>
{
    private final NonNullList<Ingredient> recipeItems;
    private final Ingredient recipeInput;
    private final ItemStack recipeOutput;
    private final ComponentBuilderBoardLayout layout;
    private final ResourceLocation id;
    private final String group;

    public ComponentBuilderRecipe(ResourceLocation id, String group, NonNullList<Ingredient> recipeItems, Ingredient recipeInput, ItemStack recipeOutput, ComponentBuilderBoardLayout layout)
    {
        this.recipeItems = recipeItems;
        this.recipeInput = recipeInput;
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
        int subIndex = 0;
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                if (!this.layout.hasSlot(1 << i + j * 3))
                {
                    subIndex++;
                    continue;
                }
                Ingredient ingredient = this.recipeItems.get(i + j * 3 - subIndex);
                if (!ingredient.test(inventory.getStackInSlot(i + j * 3)))
                {
                    return false;
                }
            }
        }

        return this.recipeInput.test(inventory.getStackInSlot(9));
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
                Ingredient ingredient = this.recipeItems.get(i + j * 3);
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

    private static NonNullList<Ingredient> deserializeIngredients(ComponentBuilderBoardLayout layout, String[] pattern, Map<String, Ingredient> keys)
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
                else if (!layout.hasSlot(j + i * 3) && ingredient == Ingredient.EMPTY)
                {
                    throw new JsonSyntaxException("Pattern has symbol '" + s + "' at '" + i + "','" + j + "' but it's not enabled in the specified layout");
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

    private static String[] shrink(String... toShrink)
    {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < toShrink.length; ++i1)
        {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0)
            {
                if (k == i1)
                {
                    ++k;
                }

                ++l;
            }
            else
            {
                l = 0;
            }
        }

        if (toShrink.length == l)
        {
            return new String[0];
        }
        else
        {
            String[] astring = new String[toShrink.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1)
            {
                astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String str)
    {
        int i;
        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i)
        {
            ;
        }

        return i;
    }

    private static int lastNonSpace(String str)
    {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i)
        {
            ;
        }

        return i;
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
        public static final ResourceLocation NAME = new ResourceLocation(OpenDevices.MOD_ID, "component_builder");

        @Override
        public ComponentBuilderRecipe read(ResourceLocation id, JsonObject json)
        {
            ResourceLocation layoutId = new ResourceLocation(JSONUtils.getString(json, "layout"));
            if (!DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.containsKey(layoutId))
                throw new JsonParseException("Could not find board layout with id '" + layoutId + "'");
            ComponentBuilderBoardLayout layout = DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.getValue(layoutId);
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = ComponentBuilderRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] ingredientsString = ComponentBuilderRecipe.shrink(ComponentBuilderRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            NonNullList<Ingredient> ingredients = ComponentBuilderRecipe.deserializeIngredients(layout, ingredientsString, map);
            Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ComponentBuilderRecipe(id, group, ingredients, input, result, layout);
        }

        @Nullable
        @Override
        public ComponentBuilderRecipe read(ResourceLocation id, PacketBuffer buffer)
        {
            ResourceLocation layoutId = buffer.readResourceLocation();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int k = 0; k < ingredients.size(); ++k)
            {
                ingredients.set(k, Ingredient.read(buffer));
            }

            Ingredient input = Ingredient.read(buffer);
            ItemStack result = buffer.readItemStack();
            return new ComponentBuilderRecipe(id, s, ingredients, input, result, DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.getValue(layoutId));
        }

        @Override
        public void write(PacketBuffer buffer, ComponentBuilderRecipe recipe)
        {
            buffer.writeResourceLocation(Objects.requireNonNull(recipe.getLayout().getRegistryName()));
            buffer.writeString(recipe.group);

            for (Ingredient ingredient : recipe.recipeItems)
            {
                ingredient.write(buffer);
            }

            recipe.recipeInput.write(buffer);
            buffer.writeItemStack(recipe.recipeOutput);
        }
    }
}
