package com.ocelot.opendevices.crafting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.crafting.ComponentBuilderLayout;
import com.ocelot.opendevices.api.crafting.ComponentBuilderLayoutManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SyncComponentBuilderLayoutsTask;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID)
public class ComponentBuilderLayoutLoader extends JsonReloadListener implements ComponentBuilderLayoutManager
{
    public static final String NAME = "component_builder_layouts";
    public static final ComponentBuilderLayoutLoader INSTANCE = new ComponentBuilderLayoutLoader();

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ComponentBuilderLayout.class, new ComponentBuilderLayout.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();

    private final Map<ResourceLocation, ComponentBuilderLayout> layouts;

    private ComponentBuilderLayoutLoader()
    {
        super(GSON, NAME);
        this.layouts = new HashMap<>();
    }

    @Override
    public boolean exists(ResourceLocation registryName)
    {
        return this.layouts.containsKey(registryName);
    }

    @Override
    public ComponentBuilderLayout getLayout(ResourceLocation registryName)
    {
        return this.layouts.get(registryName);
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return this.layouts.keySet();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> object, IResourceManager resourceManager, IProfiler profiler)
    {
        OpenDevices.LOGGER.info("Loading " + object.size() + " component builder layouts.");
        this.layouts.clear();
        for (Map.Entry<ResourceLocation, JsonObject> entry : object.entrySet())
        {
            try
            {
                this.layouts.put(entry.getKey(), GSON.fromJson(entry.getValue(), ComponentBuilderLayout.class));
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Failed to load component builder layout '" + entry.getKey() + "'", e);
            }
        }
        TaskManager.sendToAll(new SyncComponentBuilderLayoutsTask(this.layouts));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartingEvent event)
    {
        event.getServer().getResourceManager().addReloadListener(INSTANCE);
    }
}
