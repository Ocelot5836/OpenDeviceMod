package com.ocelot.opendevices.core.render.sprite;

import com.google.common.collect.Iterables;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.core.registry.WindowIconRegistryEntry;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ocelot.opendevices.api.IconManager.DEFAULT_WINDOW_ICON;

public class WindowIconSpriteUploader extends SpriteUploader
{
    public WindowIconSpriteUploader(TextureManager textureManager)
    {
        super(textureManager, IconManager.LOCATION_WINDOW_ICONS_TEXTURE, "textures");
    }

    @Override
    protected Iterable<ResourceLocation> getKnownKeys()
    {
        return Iterables.concat(DeviceRegistries.WINDOW_ICONS.getValues().stream().map(WindowIconRegistryEntry::getLocation).filter(Objects::nonNull).collect(Collectors.toSet()), Collections.singleton(DEFAULT_WINDOW_ICON));
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation icon)
    {
        return super.getSprite(icon);
    }
}