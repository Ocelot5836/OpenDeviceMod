package com.ocelot.opendevices.core.render.sprite;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.core.registry.WindowIconRegistryEntry;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.stream.Stream;

import static com.ocelot.opendevices.api.IconManager.DEFAULT_WINDOW_ICON;

@OnlyIn(Dist.CLIENT)
public class WindowIconSpriteUploader extends SpriteUploader
{
    public WindowIconSpriteUploader(TextureManager textureManager)
    {
        super(textureManager, IconManager.LOCATION_WINDOW_ICONS_TEXTURE, "");
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations()
    {
        return Stream.concat(DeviceRegistries.WINDOW_ICONS.getValues().stream().map(WindowIconRegistryEntry::getLocation).filter(Objects::nonNull), Stream.of(DEFAULT_WINDOW_ICON));
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation icon)
    {
        return super.getSprite(icon);
    }
}