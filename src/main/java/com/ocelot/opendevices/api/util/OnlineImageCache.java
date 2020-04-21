package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import io.github.ocelot.common.OnlineRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>Manages the downloading, caching, uploading, and deletion of online images.</p>
 * <p>To download and cache an image, use {@link #request(String, TimeUnit, long, BiConsumer, Consumer)}.</p>
 * <p>A cached image can be tested for expiration by calling {@link #hasExpired(String)} which checks for if the image needs to be refreshed.</p>
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID)
public class OnlineImageCache
{
    private static final CachedImage MISSING_CACHE = new CachedImage(16, 16, NativeImage.PixelFormat.RGBA, null, 0L);
    private static final Set<String> requestedUrls = new HashSet<>();
    private static final Set<String> erroredUrls = new HashSet<>();
    private static final File cacheFolder = new File(Minecraft.getInstance().gameDir, OpenDevices.MOD_ID + "-online-image-cache");
    private static final Map<String, CachedImage> cache = new HashMap<>();

    private static void writeCache(String hash, NativeImage image, long expires)
    {
        OpenDevices.LOGGER.debug("Writing image with hash '" + hash + "' to cache");
        try
        {
            File cacheFile = new File(cacheFolder, hash);
            if ((cacheFolder.exists() || cacheFolder.mkdirs()) && (cacheFile.exists() || cacheFile.createNewFile()))
            {
                image.write(cacheFile);
                cache.put(hash, new CachedImage(image.getWidth(), image.getHeight(), image.getFormat(), cacheFile, expires));
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
        OpenDevices.LOGGER.debug("Reading image with hash '" + hash + "' from cache");
        try
        {
            FileInputStream stream = new FileInputStream(cache.get(hash).getFile());
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

    private static void catchError(Exception e, String url, BiConsumer<ResourceLocation, CachedImage> callback, Consumer<Exception> errorCallback)
    {
        if (errorCallback != null)
        {
            errorCallback.accept(e);
        }
        else
        {
            OpenDevices.LOGGER.error("Could not load cache image from '" + url + "'. Using missing texture sprite.", e);
        }
        requestedUrls.remove(url);
        erroredUrls.add(url);
        callback.accept(MissingTextureSprite.getLocation(), MISSING_CACHE);
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
        return !cache.containsKey(hash) || System.currentTimeMillis() - cache.get(hash).getExpires() > 0;
    }

    /**
     * Requests an image from the specified URL.
     *
     * @param url           The url to fetch the image from
     * @param unit          The time unit used for cache time
     * @param cacheTime     The amount of time the image saves for
     * @param callback      The callback for when the location is available
     * @param errorCallback The callback for when an error occurs or null if nothing custom should happen
     */
    public static void request(String url, TimeUnit unit, long cacheTime, BiConsumer<ResourceLocation, CachedImage> callback, Consumer<Exception> errorCallback)
    {
        if (erroredUrls.contains(url))
        {
            callback.accept(MissingTextureSprite.getLocation(), MISSING_CACHE);
            return;
        }
        if (requestedUrls.contains(url))
            return;
        requestedUrls.add(url);
        String hash = DigestUtils.md5Hex(url);
        if (cacheTime == 0 || !hasExpired(url))
        {
            ResourceLocation location = new ResourceLocation(hash);
            if (Minecraft.getInstance().getTextureManager().getTexture(location) == null)
            {
                NativeImage image = readCache(hash);
                if (image != null)
                {
                    Minecraft.getInstance().execute(() ->
                    {
                        Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                        callback.accept(location, cache.getOrDefault(hash, MISSING_CACHE));
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
                callback.accept(location, cache.getOrDefault(hash, MISSING_CACHE));
                requestedUrls.remove(url);
                return;
            }
        }
        OpenDevices.LOGGER.debug("Requesting Online Image from '" + url + "' with hash '" + hash + "'");
        OnlineRequest.make(url, inputStream ->
        {
            try
            {
                if (inputStream == null)
                    throw new IllegalArgumentException("Could not read image from '" + url + "' with hash '" + hash + "'");
                NativeImage image = NativeImage.read(inputStream);
                writeCache(hash, image, System.currentTimeMillis() + unit.toMillis(cacheTime));
                Minecraft.getInstance().execute(() ->
                {
                    ResourceLocation location = new ResourceLocation(hash);
                    Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                    callback.accept(location, cache.getOrDefault(hash, MISSING_CACHE));
                    requestedUrls.remove(url);
                });
            }
            catch (Exception e)
            {
                catchError(e, url, callback, errorCallback);
            }
        }, e -> catchError(e, url, callback, errorCallback));
    }

    /**
     * Deletes the cache for the specified URL.
     *
     * @param url The url of the image to erase
     */
    public static void delete(String url)
    {
        String hash = DigestUtils.md5Hex(url);
        OpenDevices.LOGGER.debug("Deleting Online Image '" + url + "' with hash '" + hash + "'");
        if (cache.containsKey(hash))
        {
            cache.remove(hash);
            erroredUrls.remove(url);
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().deleteTexture(new ResourceLocation(hash)));
        }
    }

    /**
     * Deletes all cached images and textures.
     */
    @SubscribeEvent
    public static void clear(WorldEvent.Unload event)
    {
        OpenDevices.LOGGER.debug("Clearing Online Image Cache");

        Minecraft.getInstance().execute(() ->
        {
            cache.forEach((hash, pair) -> Minecraft.getInstance().getTextureManager().deleteTexture(new ResourceLocation(hash)));
            cache.clear();
            erroredUrls.clear();
            if (!cacheFolder.delete())
            {
                OpenDevices.LOGGER.warn("Could not delete cache folder '" + cacheFolder.getPath() + "'");
            }
        });
    }

    /**
     * <p>An image that has been downloaded from the internet and written to file.</p>
     *
     * @author Ocelot
     */
    public static class CachedImage
    {
        private final int width;
        private final int height;
        private final NativeImage.PixelFormat format;
        private final File file;
        private final long expires;

        private CachedImage(int width, int height, NativeImage.PixelFormat format, File file, long expires)
        {
            this.width = width;
            this.height = height;
            this.format = format;
            this.file = file;
            this.expires = expires;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public NativeImage.PixelFormat getFormat()
        {
            return format;
        }

        public File getFile()
        {
            return file;
        }

        public long getExpires()
        {
            return expires;
        }
    }
}