package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.DeviceRegistries;
import com.ocelot.opendevices.core.registry.ComponentBuilderBoardTexture;
import com.ocelot.opendevices.core.registry.WindowIconRegistryEntry;
import com.ocelot.opendevices.core.render.sprite.OpenDevicesSpriteUploader;
import com.ocelot.opendevices.crafting.ComponentBuilderLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Manages the texture map containing all textures used.</p>
 *
 * @author Ocelot
 */
public class IconManager
{
    public static final ResourceLocation LOCATION_OPENDEVICES_GUI_ATLAS = new ResourceLocation(OpenDevices.MOD_ID, "atlas/textures.png");
    public static final ResourceLocation DEFAULT_WINDOW_ICON = new ResourceLocation(OpenDevices.MOD_ID, "app/icon/default");

    @OnlyIn(Dist.CLIENT)
    private static OpenDevicesSpriteUploader openDevicesSpriteUploader;

    private IconManager() {}

    private static void registerSprites(IResourceManager resourceManager, OpenDevicesSpriteUploader uploader)
    {
        uploader.registerSprite(DEFAULT_WINDOW_ICON);
        DeviceRegistries.WINDOW_ICONS.getValues().stream().map(WindowIconRegistryEntry::getLocation).filter(Objects::nonNull).distinct().forEach(uploader::registerSprite);
        DeviceRegistries.COMPONENT_BUILDER_BOARD_TEXTURES.getValues().stream().map(ComponentBuilderBoardTexture::getTextureLocation).filter(Objects::nonNull).distinct().forEach(uploader::registerSprite);
        uploader.registerSpriteSupplier(() -> resourceManager.getAllResourceLocations("textures/component_builder_layout", s -> s.endsWith(".png")).stream().map(resourceLocation -> new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().substring(9, resourceLocation.getPath().length() - 4))).collect(Collectors.toSet()));
    }

    /**
     * Core Usage Only.
     */
    @OnlyIn(Dist.CLIENT)
    public static void init(IEventBus bus)
    {
        bus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event ->
        {
            Minecraft minecraft = Minecraft.getInstance();
            OpenDevicesSpriteUploader spriteUploader = new OpenDevicesSpriteUploader(minecraft.textureManager);
            IResourceManager resourceManager = minecraft.getResourceManager();
            registerSprites(resourceManager, spriteUploader);
            if (resourceManager instanceof IReloadableResourceManager)
            {
                ((IReloadableResourceManager) resourceManager).addReloadListener(spriteUploader);
            }
            openDevicesSpriteUploader = spriteUploader;
        });
    }

    /**
     * Checks the texture map for the specified window icon.
     *
     * @param icon The key of the icon
     * @return The sprite for the specified key
     */
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getWindowIcon(@Nullable ResourceLocation icon)
    {
        return openDevicesSpriteUploader.getSprite(icon == null ? DEFAULT_WINDOW_ICON : icon);
    }

    /**
     * Checks the texture map for the board texture from the specified item.
     *
     * @param item The item to get the sprite from
     * @return The sprite for the specified key
     */
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getBoardTexture(Item item)
    {
        ResourceLocation texture = DeviceRegistries.getBoardTextureLocation(item);
        return openDevicesSpriteUploader.getSprite(texture == null ? TextureManager.RESOURCE_LOCATION_EMPTY : texture);
    }

    /**
     * Checks the texture map for the specified layout texture.
     *
     * @param layout The layout to get the sprite from
     * @return The sprite for the specified key
     */
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getLayoutTexture(ComponentBuilderLayout layout)
    {
        return openDevicesSpriteUploader.getSprite(layout == null || layout.getTextureLocation() == null ? TextureManager.RESOURCE_LOCATION_EMPTY : layout.getTextureLocation());
    }
}
