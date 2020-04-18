package com.ocelot.opendevices.jei;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.crafting.ComponentBuilderRecipe;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.gui.AbstractGui.blit;

public class ComponentBuilderRecipeCategory implements IRecipeCategory<ComponentBuilderRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ComponentBuilderRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(OpenDevicesJeiPlugin.RECIPE_GUI_OPENDEVICES, 0, 0, 156, 64);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(DeviceBlocks.COMPONENT_BUILDER));
        this.localizedName = I18n.format("gui." + OpenDevices.MOD_ID + ".category.component_builder");
    }

    @Override
    public ResourceLocation getUid()
    {
        return OpenDevicesJeiPlugin.COMPONENT_BUILDER_CATEGORY_ID;
    }

    @Override
    public Class<? extends ComponentBuilderRecipe> getRecipeClass()
    {
        return ComponentBuilderRecipe.class;
    }

    @Override
    public String getTitle()
    {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground()
    {
        return this.background;
    }

    @Override
    public IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setIngredients(ComponentBuilderRecipe recipe, IIngredients ingredients)
    {
        List<Ingredient> ingredientList = new ArrayList<>(recipe.getIngredients());
        ingredientList.add(recipe.getRecipeInput());
        ingredientList.add(Ingredient.fromStacks(new ItemStack(DeviceItems.SOLDER, recipe.getSolderAmount())));

        ingredients.setInputIngredients(ingredientList);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ComponentBuilderRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                int index = x + y * 3;
                guiItemStacks.init(index, true, 2 + x * 21, 2 + y * 21);
                guiItemStacks.set(index, inputs.get(index));
            }
        }

        guiItemStacks.init(9, true, 106, 10);
        guiItemStacks.set(9, inputs.get(9));
        guiItemStacks.init(10, false, 138, 0);
        guiItemStacks.set(10, new ItemStack(DeviceItems.SOLDER_IRON));
        guiItemStacks.init(11, false, 138, 22);
        guiItemStacks.set(11, inputs.get(10));

        guiItemStacks.init(0, false, 106, 36);
        guiItemStacks.set(0, outputs.get(0));
    }

    @Override
    public void draw(ComponentBuilderRecipe recipe, double mouseX, double mouseY)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(IconManager.LOCATION_OPENDEVICES_GUI_ATLAS);
        blit(0, 0, 0, 64, 64, IconManager.getBoardTexture(Arrays.stream(recipe.getRecipeInput().getMatchingStacks()).findFirst().orElse(ItemStack.EMPTY).getItem()));
        blit(0, 0, 0, 64, 64, IconManager.getLayoutTexture(recipe.getLayout()));
    }
}
