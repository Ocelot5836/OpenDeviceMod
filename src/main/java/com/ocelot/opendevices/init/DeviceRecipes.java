package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.crafting.component_builder.ComponentBuilderRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

public class DeviceRecipes
{
    public static final IRecipeType<ComponentBuilderRecipe> COMPONENT_BUILDER = IRecipeType.register(OpenDevices.MOD_ID + ":component_builder");

    public static final IRecipeSerializer<ComponentBuilderRecipe> COMPONENT_BUILDER_SERIALIZER = new ComponentBuilderRecipe.Serializer();

}
