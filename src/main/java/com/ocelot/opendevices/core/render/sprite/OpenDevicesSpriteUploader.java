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

@OnlyIn(Dist.CLIENT)
public class OpenDevicesSpriteUploader extends SpriteUploader
{
    public OpenDevicesSpriteUploader(TextureManager textureManager)
    {
        super(textureManager, IconManager.LOCATION_OPENDEVICES_TEXTURES, "");
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations()
    {
        return Stream.concat(DeviceRegistries.WINDOW_ICONS.getValues().stream().map(WindowIconRegistryEntry::getLocation).filter(Objects::nonNull).distinct(), Stream.of(IconManager.DEFAULT_WINDOW_ICON));
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation icon)
    {
        return super.getSprite(icon);
    }
}