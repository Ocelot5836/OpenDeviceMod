package com.ocelot.opendevices.core.render;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.core.registry.ComponentBuilderBoardTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ComponentBuilderBoardTextureManager
{
    public static final ResourceLocation LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/atlas/board_textures.png");

    private static AtlasTexture atlas;
    private static boolean initialized;

    private static void createAtlas()
    {
        atlas = new AtlasTexture("textures");
        Minecraft.getInstance().getTextureManager().loadTickableTexture(LOCATION, atlas);
    }

    public static void init()
    {
        if (!initialized)
        {
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener());
            initialized = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBoardLayouts(RegistryEvent.Register<ComponentBuilderBoardLayout> event)
    {
        init();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBoardTextures(RegistryEvent.Register<ComponentBuilderBoardTexture> event)
    {
        init();
    }

    public static TextureAtlasSprite getBoardTexture(Item item)
    {
        ResourceLocation texture = DeviceRegistries.getBoardTextureLocation(item);
        return atlas.getSprite(texture != null ? texture : TextureManager.RESOURCE_LOCATION_EMPTY);
    }

    public static TextureAtlasSprite getLayoutTexture(ComponentBuilderBoardLayout layout)
    {
        return atlas.getSprite(layout != null ? layout.getTextureLocation() : TextureManager.RESOURCE_LOCATION_EMPTY);
    }

    private static class ReloadListener implements IFutureReloadListener
    {
        @Override
        public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
        {
            Set<ResourceLocation> iconLocations = new HashSet<>();
            Set<CompletableFuture<?>> completablefuture = new HashSet<>();
            completablefuture.addAll(DeviceRegistries.COMPONENT_BUILDER_BOARD_TEXTURES.getEntries().stream().map(entry -> CompletableFuture.runAsync(() -> iconLocations.add(entry.getValue().getTextureLocation()), backgroundExecutor)).collect(Collectors.toSet()));
            completablefuture.addAll(DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.getEntries().stream().map(entry -> CompletableFuture.runAsync(() -> iconLocations.add(entry.getValue().getTextureLocation()), backgroundExecutor)).collect(Collectors.toSet()));
            
            return CompletableFuture.allOf(completablefuture.toArray(new CompletableFuture[0])).thenApplyAsync(v ->
            {
                preparationsProfiler.startTick();
                preparationsProfiler.startSection("stitching");

                if (atlas == null)
                {
                    createAtlas();
                }

                AtlasTexture.SheetData sheetData = atlas.stitch(resourceManager, iconLocations, preparationsProfiler);

                preparationsProfiler.endSection();
                preparationsProfiler.endTick();
                return sheetData;
            }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(sheetData ->
            {
                reloadProfiler.startTick();
                reloadProfiler.startSection("upload");

                atlas.upload(sheetData);

                reloadProfiler.endSection();
                reloadProfiler.endTick();
            }, gameExecutor);
        }
    }
}
