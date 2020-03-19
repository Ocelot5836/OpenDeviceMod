package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.core.registry.WindowIconRegistryEntry;
import com.ocelot.opendevices.core.render.sprite.WindowIconSpriteUploader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IconManager
{
    public static final ResourceLocation LOCATION_WINDOW_ICONS_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "textures/atlas/app_icons.png");
    public static final ResourceLocation DEFAULT_WINDOW_ICON = new ResourceLocation(OpenDevices.MOD_ID, "app/icon/default");

    private static WindowIconSpriteUploader windowIconSpriteUploader;

    private IconManager() {}

    /**
     * Core Usage Only.
     */
    public static void init()
    {
        Minecraft minecraft = Minecraft.getInstance();
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) minecraft.getResourceManager();
        resourceManager.addReloadListener((stage, iResourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
        {
            if (windowIconSpriteUploader == null)
                windowIconSpriteUploader = new WindowIconSpriteUploader(minecraft.textureManager);
            return windowIconSpriteUploader.reload(stage, iResourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
        });
    }

    /**
     * Checks the texture map for the specified window icon.
     *
     * @param icon The key of the icon
     * @return The sprite for the specified key
     */
    public static TextureAtlasSprite getWindowIcon(ResourceLocation icon)
    {
        return windowIconSpriteUploader.getSprite(icon == null ? DEFAULT_WINDOW_ICON : icon);
    }
}
