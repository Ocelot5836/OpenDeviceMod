package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.core.render.sprite.WindowIconSpriteUploader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public class IconManager
{
    public static final ResourceLocation LOCATION_WINDOW_ICONS_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "atlas/app_icons.png");
    public static final ResourceLocation DEFAULT_WINDOW_ICON = new ResourceLocation(OpenDevices.MOD_ID, "app/icon/default");

    @OnlyIn(Dist.CLIENT)
    private static WindowIconSpriteUploader windowIconSpriteUploader;

    private IconManager() {}

    /**
     * Core Usage Only.
     */
    @OnlyIn(Dist.CLIENT)
    public static void init(IEventBus bus)
    {
        bus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event ->
        {
            Minecraft minecraft = Minecraft.getInstance();
            WindowIconSpriteUploader spriteUploader = new WindowIconSpriteUploader(minecraft.textureManager);
            IResourceManager resourceManager = minecraft.getResourceManager();
            if (resourceManager instanceof IReloadableResourceManager)
            {
                IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) resourceManager;
                reloadableResourceManager.addReloadListener(spriteUploader);
            }
            windowIconSpriteUploader = spriteUploader;
        });
    }

    /**
     * Checks the texture map for the specified window icon.
     *
     * @param icon The key of the icon
     * @return The sprite for the specified key
     */
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getWindowIcon(ResourceLocation icon)
    {
        return windowIconSpriteUploader.getSprite(icon == null ? DEFAULT_WINDOW_ICON : icon);
    }
}
