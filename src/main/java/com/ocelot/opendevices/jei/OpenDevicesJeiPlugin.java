package com.ocelot.opendevices.jei;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.container.ComponentBuilderContainer;
import com.ocelot.opendevices.container.ComponentBuilderScreen;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class OpenDevicesJeiPlugin implements IModPlugin
{
    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(OpenDevices.MOD_ID, OpenDevices.MOD_ID);
    public static final ResourceLocation COMPONENT_BUILDER_CATEGORY_ID = new ResourceLocation(OpenDevices.MOD_ID, "component_builder");
    public static final ResourceLocation RECIPE_GUI_OPENDEVICES = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/jei.png");

    @Override
    public ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new ComponentBuilderRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        ClientWorld world = Minecraft.getInstance().world;
        RecipeManager recipeManager = world.getRecipeManager();
        Stream<IRecipe<?>> recipes = recipeManager.getRecipes().stream();
        Set<IRecipe<?>> componentBuilderRecipes = recipes.filter(recipe -> recipe.getType() == DeviceRecipes.COMPONENT_BUILDER).collect(Collectors.toSet());
        registration.addRecipes(componentBuilderRecipes, COMPONENT_BUILDER_CATEGORY_ID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(ComponentBuilderScreen.class, 76, 51, 28, 23, COMPONENT_BUILDER_CATEGORY_ID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(ComponentBuilderContainer.class, COMPONENT_BUILDER_CATEGORY_ID, 1, 9, 13, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(DeviceBlocks.COMPONENT_BUILDER), COMPONENT_BUILDER_CATEGORY_ID);
    }
}
