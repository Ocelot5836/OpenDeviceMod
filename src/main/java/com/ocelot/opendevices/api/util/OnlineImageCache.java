package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <p>Manages the downloading, caching, uploading, and deletion of online images.</p>
 * <p>To download and cache an image, use {@link #request(String, long, Consumer, Consumer)}.</p>
 * <p>A cached image can be tested for expiration by calling {@link #hasExpired(String)} which checks for if the image needs to be refreshed.</p>
 */
public class OnlineImageCache
{
    private static final Set<String> requestedUrls = new HashSet<>();
    private static final File cacheFolder = new File(Minecraft.getInstance().gameDir, OpenDevices.MOD_ID + "-online-image-cache");
    private static final Map<String, Pair<File, Long>> cache = new HashMap<>();

    private static void cache(String hash, NativeImage image, long expires)
    {
        try
        {
            File cacheFile = new File(cacheFolder, hash);
            if ((cacheFolder.exists() || cacheFolder.mkdirs()) && (cacheFile.exists() || cacheFile.createNewFile()))
            {
                image.write(cacheFile);
                cache.put(hash, new ImmutablePair<>(cacheFile, expires));
            }
        }
        catch (IOException e)
        {
            OpenDevices.LOGGER.error("Could not cache image with hash '" + hash + "'.", e);
        }
    }

    @Nullable
    private static NativeImage readCache(String hash)
    {
        if (!cache.containsKey(hash))
            return null;
        try
        {
            FileInputStream stream = new FileInputStream(cache.get(hash).getLeft());
            NativeImage image = NativeImage.read(stream);
            stream.close();
            return image;
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not read image with hash '" + hash + "' from cache.", e);
            return null;
        }
    }

    /**
     * Checks for if an image needs to be refreshed the next time it is requested.
     *
     * @param url The url to download the image from
     * @return Whether or not the image has expired
     */
    public static boolean hasExpired(String url)
    {
        String hash = DigestUtils.md5Hex(url);
        return !cache.containsKey(hash) || System.currentTimeMillis() - cache.get(hash).getRight() > 0;
    }

    /**
     * Requests an image from the specified URL.
     *
     * @param url           The url to fetch the image from
     * @param cacheTime     The amount of time the image saves for
     * @param callback      The callback for when the location is available
     * @param errorCallback The callback for when an error occurs or null if nothing custom should happen
     */
    public static void request(String url, long cacheTime, Consumer<ResourceLocation> callback, Consumer<IOException> errorCallback)
    {
        if (requestedUrls.contains(url))
            return;
        String hash = DigestUtils.md5Hex(url);
        if (cacheTime == 0 || !hasExpired(url))
        {
            ResourceLocation location = new ResourceLocation(hash);
            if (Minecraft.getInstance().getTextureManager().getTexture(location) == null)
            {
                NativeImage image = readCache(hash);
                if (image != null)
                {
                    requestedUrls.add(url);
                    Minecraft.getInstance().execute(() ->
                    {
                        Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                        callback.accept(location);
                        requestedUrls.remove(url);
                    });
                    return;
                }
                else
                {
                    cache.remove(hash);
                }
            }
            else
            {
                callback.accept(location);
                return;
            }
        }
        requestedUrls.add(url);
        OnlineRequest.make(url, inputStream ->
        {
            try
            {
                NativeImage image = NativeImage.read(inputStream);
                cache(hash, image, System.currentTimeMillis() + cacheTime);
                Minecraft.getInstance().execute(() ->
                {
                    ResourceLocation location = new ResourceLocation(hash);
                    Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                    callback.accept(location);
                    requestedUrls.remove(url);
                });
            }
            catch (IOException e)
            {
                if (errorCallback != null)
                {
                    errorCallback.accept(e);
                }
                else
                {
                    OpenDevices.LOGGER.error("Could not load cache image from '" + url + "'.", e);
                }
                callback.accept(MissingTextureSprite.getLocation());
            }
        });
    }

    /**
     * Deletes the cache for the specified URL.
     *
     * @param url The url of the image to erase
     */
    public static void delete(String url)
    {
        String hash = DigestUtils.md5Hex(url);
        if (cache.containsKey(hash))
        {
            cache.remove(hash);
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().deleteTexture(new ResourceLocation(hash)));
        }
    }
}