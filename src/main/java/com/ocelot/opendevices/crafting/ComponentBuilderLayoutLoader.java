package com.ocelot.opendevices.crafting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SyncComponentBuilderLayoutsTask;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID)
public class ComponentBuilderLayoutLoader extends JsonReloadListener implements ComponentBuilderLayoutManager
{
    public static final String NAME = "component_builder_layouts";
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ComponentBuilderLayout.class, new ComponentBuilderLayout.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();

    private static ComponentBuilderLayoutLoader instance;
    private final Map<ResourceLocation, ComponentBuilderLayout> layouts;

    private ComponentBuilderLayoutLoader()
    {
        super(GSON, NAME);
        this.layouts = new HashMap<>();
        instance = this;
    }

    @Override
    public boolean exists(ResourceLocation registryName)
    {
        return this.layouts.containsKey(registryName);
    }

    @Override
    public ComponentBuilderLayout getLayout(ResourceLocation registryName)
    {
        return this.layouts.getOrDefault(registryName, ComponentBuilderLayout.EMPTY);
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return this.layouts.keySet();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> object, IResourceManager resourceManager, IProfiler profiler)
    {
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
        OpenDevices.LOGGER.info("Loaded " + this.layouts.size() + " component builder layout(s) with " + (object.size() - this.layouts.size()) + " error(s).");
        TaskManager.sendToAll(new SyncComponentBuilderLayoutsTask(this.layouts));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerAboutToStartEvent event)
    {
        event.getServer().getResourceManager().addReloadListener(new ComponentBuilderLayoutLoader());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppingEvent event)
    {
        instance = null;
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
        {
            TaskManager.sendToClient(new SyncComponentBuilderLayoutsTask(instance.layouts), (ServerPlayerEntity) event.getPlayer(), false);
        }
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
        {
            TaskManager.sendToClient(new SyncComponentBuilderLayoutsTask(Collections.emptyMap()), (ServerPlayerEntity) event.getPlayer(), false);
        }
    }

    public static ComponentBuilderLayoutManager instance()
    {
        return instance == null ? instance = new ComponentBuilderLayoutLoader() : instance;
    }
}
