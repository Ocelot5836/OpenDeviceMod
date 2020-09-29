package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class DeviceRecipes
{
    private static final Set<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new HashSet<>();

    public static final IRecipeType<ComponentBuilderRecipe> COMPONENT_BUILDER = IRecipeType.register(OpenDevices.MOD_ID + ":component_builder");
    public static final IRecipeSerializer<ComponentBuilderRecipe> COMPONENT_BUILDER_SERIALIZER = register("component_builder", new ComponentBuilderRecipe.Serializer());

    public static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String registryName, S serializer)
    {
        RECIPE_SERIALIZERS.add(serializer.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName)));
        return serializer;
    }

    public static IRecipeSerializer<?>[] getRecipeSerializers()
    {
        return RECIPE_SERIALIZERS.toArray(new IRecipeSerializer[0]);
    }
}
