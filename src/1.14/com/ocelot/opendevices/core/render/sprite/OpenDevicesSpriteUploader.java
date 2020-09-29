package com.ocelot.opendevices.core.render.sprite;

import com.ocelot.opendevices.api.IconManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class OpenDevicesSpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable
{
    private final AtlasTexture textureAtlas;
    private final Set<ResourceLocation> registeredSprites;
    private final Set<Supplier<Collection<ResourceLocation>>> registeredSpriteSuppliers;

    public OpenDevicesSpriteUploader(TextureManager textureManager)
    {
        this.textureAtlas = new AtlasTexture(IconManager.LOCATION_OPENDEVICES_GUI_ATLAS);
        this.registeredSprites = new HashSet<>();
        this.registeredSpriteSuppliers = new HashSet<>();
        textureManager.loadTexture(this.textureAtlas.getTextureLocation(), this.textureAtlas);
    }

    public void registerSprite(ResourceLocation location)
    {
        this.registeredSprites.add(location);
    }

    public void registerSpriteSupplier(Supplier<Collection<ResourceLocation>> location)
    {
        this.registeredSpriteSuppliers.add(location);
    }

    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return this.textureAtlas.getSprite(location);
    }

    @Override
    protected AtlasTexture.SheetData prepare(IResourceManager resourceManager, IProfiler profiler)
    {
        profiler.startTick();
        profiler.startSection("stitching");
        AtlasTexture.SheetData sheetData = this.textureAtlas.stitch(resourceManager, this.getResourceLocations(), profiler, 0);
        profiler.endSection();
        profiler.endTick();
        return sheetData;
    }

    @Override
    protected void apply(AtlasTexture.SheetData object, IResourceManager resourceManager, IProfiler profiler)
    {
        profiler.startTick();
        profiler.startSection("upload");
        this.textureAtlas.upload(object);
        profiler.endSection();
        profiler.endTick();
    }

    @Override
    public void close()
    {
        this.textureAtlas.clear();
    }

    private Stream<ResourceLocation> getResourceLocations()
    {
        Set<ResourceLocation> locations = new HashSet<>(this.registeredSprites);
        this.registeredSpriteSuppliers.stream().map(Supplier::get).forEach(locations::addAll);
        return Collections.unmodifiableSet(locations).stream();
    }
}