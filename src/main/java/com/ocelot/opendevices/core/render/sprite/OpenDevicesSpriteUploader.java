package com.ocelot.opendevices.core.render.sprite;

import com.ocelot.opendevices.api.DeviceResourceTypes;
import com.ocelot.opendevices.api.IconManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class OpenDevicesSpriteUploader extends SpriteUploader
{
    private final Set<ResourceLocation> registeredSprites = new HashSet<>();

    public OpenDevicesSpriteUploader(TextureManager textureManager)
    {
        super(textureManager, IconManager.LOCATION_OPENDEVICES_GUI_ATLAS, "");
    }

    public void registerSprite(ResourceLocation location)
    {
        this.registeredSprites.add(location);
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations()
    {
        return Collections.unmodifiableSet(this.registeredSprites).stream();
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }

}