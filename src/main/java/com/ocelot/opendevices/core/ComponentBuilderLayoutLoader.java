package com.ocelot.opendevices.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.crafting.ComponentBuilderLayoutManager;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.Map;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID)
public class ComponentBuilderLayoutLoader extends JsonReloadListener implements ComponentBuilderLayoutManager
{
    public static final String NAME = "component_builder_board_layouts";
    public static final ComponentBuilderLayoutLoader INSTANCE = new ComponentBuilderLayoutLoader();

    private static final Gson GSON = new GsonBuilder().create();

    private ComponentBuilderLayoutLoader()
    {
        super(GSON, NAME);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {

    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartingEvent event)
    {
        event.getServer().getResourceManager().addReloadListener(INSTANCE);
    }
}
