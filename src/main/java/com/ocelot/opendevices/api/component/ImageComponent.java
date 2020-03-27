package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.util.*;
import com.ocelot.opendevices.api.util.icon.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * <p>Allows the addition of different types of images to a {@link Layout}. Local {@link ResourceLocation} textures, online images, and {@link IIcon} are supported as image types.</p>
 * <p>In order to use a resource location as an image, use one of {@link #with(ResourceLocation, float, float, float, float)} or {@link #with(ResourceLocation, float, float, float, float, int, int)}.</p>
 * <p>Icons only requiring the use of {@link #with(IIcon)}.</p>
 * <p>Online images can be made using {@link #with(String)}, {@link #with(String, float, float, float, float)}, or {@link #with(String, float, float, float, float, TimeUnit, long)}. Online images take time to download, so a {@link SpinnerComponent} is rendered in the center until the download has completed.</p>
 *
 * @author Ocelot
 * @see ImageProvider
 * @see ResourceLocationImageProvider
 * @see IconImageProvider
 * @see OnlineImageProvider
 */
public class ImageComponent extends StandardComponent
{
    public static final int DEFAULT_COLOR = 0xFF2F2F2F;

    private float x;
    private float y;
    private int width;
    private int height;
    private int backgroundColor;
    private boolean visible;

    private ImageFit imageFit;
    private ImageProvider imageProvider;

    private int progress;

    public ImageComponent(float x, float y, int width, int height, ImageProvider imageProvider)
    {
        this.setValueSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = DEFAULT_COLOR;
        this.visible = true;

        this.imageFit = ImageFit.STRETCH;
        this.imageProvider = imageProvider;
    }

    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("width", nbt -> nbt.putInt("width", this.width), nbt -> this.width = nbt.getInt("width"));
            syncHelper.addSerializer("height", nbt -> nbt.putInt("height", this.height), nbt -> this.height = nbt.getInt("height"));
            syncHelper.addSerializer("backgroundColor", nbt -> nbt.putInt("backgroundColor", this.backgroundColor), nbt -> this.backgroundColor = nbt.getInt("backgroundColor"));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));

            syncHelper.addSerializer("imageFit", nbt -> nbt.putByte("imageFit", this.imageFit.serialize()), nbt -> this.imageFit = ImageFit.deserialize(nbt.getByte("imageFit")));
            syncHelper.addSerializer("imageProvider", nbt ->
            {
                nbt.putString("imageProviderType", this.imageProvider.getType().getRegistryName());
                nbt.put("imageProvider", this.imageProvider.serializeNBT());
            }, nbt -> this.imageProvider = ImageType.byName(nbt.getString("imageProviderType")).apply(nbt.getCompound("imageProvider")));
        }
        return syncHelper;
    }

    @Override
    public void update()
    {
        this.imageProvider.update();
        if (this.imageProvider.getLocation() != null)
        {
            this.progress = 0;
        }
        else
        {
            if (this.progress >= SpinnerComponent.MAX_PROGRESS)
            {
                this.progress = 0;
            }
            this.progress++;
        }
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        if (this.visible)
        {
            if (this.imageProvider.getLocation() != null)
            {
                Minecraft.getInstance().getTextureManager().bindTexture(this.imageProvider.getLocation());
                RenderUtil.drawRectWithTexture(posX + this.x, posY + this.y, this.imageProvider.getU(), this.imageProvider.getV(), this.width, this.height, this.imageProvider.getTextureWidth(), this.imageProvider.getTextureHeight(), this.imageProvider.getSourceWidth(), this.imageProvider.getSourceHeight(), this.imageFit);
            }
            else
            {
                this.imageProvider.request();
                GlStateManager.pushMatrix();
                GlStateManager.translatef(posX + this.x, posY + this.y, 0);
                fill(0, 0, this.width, this.height, this.backgroundColor);
                SpinnerComponent.renderProgress((this.width - SpinnerComponent.SIZE) / 2f, (this.height - SpinnerComponent.SIZE) / 2f, 0, 0xFFFFFFFF, this.progress);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void onClose()
    {
        this.imageProvider.free();
    }

    @Override
    public void onLayoutUnload()
    {
        this.imageProvider.free();
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    /**
     * @return The color of the background
     */
    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * @return Whether or not this component can be seen and interacted with
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * @return How the rendered image should fit to the component size
     */
    public ImageFit getImageFit()
    {
        return imageFit;
    }

    /**
     * @return The provider for rendering parameters
     */
    public ImageProvider getImageProvider()
    {
        return imageProvider;
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public ImageComponent setX(float x)
    {
        this.x = x;
        this.getValueSerializer().markDirty("x");
        return this;
    }

    /**
     * Sets the y position of this component to the specified value.
     *
     * @param y The new y position
     */
    public ImageComponent setY(float y)
    {
        this.y = y;
        this.getValueSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the position of this component to the specified values.
     *
     * @param x The new x position
     * @param y The new y position
     */
    public ImageComponent setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.getValueSerializer().markDirty("x");
        this.getValueSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the width of this component to the specified value.
     *
     * @param width The new x size
     */
    public ImageComponent setWidth(int width)
    {
        this.width = width;
        this.getValueSerializer().markDirty("width");
        return this;
    }

    /**
     * Sets the height of this component to the specified value.
     *
     * @param height The new y size
     */
    public ImageComponent setHeight(int height)
    {
        this.height = height;
        this.getValueSerializer().markDirty("height");
        return this;
    }

    /**
     * Sets the size of this component to the specified values.
     *
     * @param width  The new x size
     * @param height The new y size
     */
    public ImageComponent setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.getValueSerializer().markDirty("height");
        return this;
    }

    /**
     * Sets the color of the background when there is no image available yet.
     *
     * @param backgroundColor The new color of the background
     */
    public ImageComponent setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
        this.getValueSerializer().markDirty("backgroundColor");
        return this;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public ImageComponent setVisible(boolean visible)
    {
        this.visible = visible;
        this.getValueSerializer().markDirty("visible");
        return this;
    }

    /**
     * Sets how the image orients itself in the component frame.
     *
     * @param imageFit The new way to orient the image
     */
    public ImageComponent setImageFit(ImageFit imageFit)
    {
        this.imageFit = imageFit;
        this.getValueSerializer().markDirty("imageFit");
        return this;
    }

    /**
     * <p>Specifies the types of images that can be rendered inside this component.</p>
     *
     * @author Ocelot
     */
    public enum ImageType implements Function<CompoundNBT, ImageProvider>
    {
        RESOURCE_LOCATION("resource_location", ResourceLocationImageProvider::new), ICON("icon", IconImageProvider::new), ONLINE("online", OnlineImageProvider::new);

        private static final Map<String, ImageType> NAME_LOOKUP = new HashMap<>();

        private String registryName;
        private Function<CompoundNBT, ImageProvider> creator;

        ImageType(String registryName, Function<CompoundNBT, ImageProvider> creator)
        {
            this.registryName = registryName;
            this.creator = creator;
        }

        /**
         * @return The registry name of this background type
         */
        public String getRegistryName()
        {
            return registryName;
        }

        @Override
        public ImageProvider apply(CompoundNBT nbt)
        {
            return this.creator.apply(nbt);
        }

        /**
         * Fetches a desktop background type from name.
         *
         * @param registryName The registry name of the background type
         * @return The desktop background type with that registry name or {@link #RESOURCE_LOCATION} if there was an error
         */
        public static ImageType byName(String registryName)
        {
            return NAME_LOOKUP.getOrDefault(registryName.toLowerCase(Locale.ROOT), RESOURCE_LOCATION);
        }

        static
        {
            for (ImageType type : ImageType.values())
            {
                NAME_LOOKUP.put(type.getRegistryName().toLowerCase(Locale.ROOT), type);
            }
        }
    }

    /**
     * <p>A standard provider for a renderable image that can be configured.</p>
     *
     * @author Ocelot
     */
    public interface ImageProvider extends INBTSerializable<CompoundNBT>
    {
        /**
         * Called each update to update the contents.
         */
        default void update()
        {
        }

        /**
         * Requests this image to be loaded. Only called if {@link #getLocation()} returns null.
         */
        default void request()
        {
        }

        /**
         * Frees resources used by this image.
         */
        default void free()
        {
        }

        /**
         * @return The location of the image to render or null if this image is not yet ready to render
         */
        @Nullable
        ResourceLocation getLocation();

        /**
         * @return The x position on the texture to start
         */
        float getU();

        /**
         * @return The y position on the texture to start
         */
        float getV();

        /**
         * @return The x size on the texture to render
         */
        float getTextureWidth();

        /**
         * @return The y size on the texture to render
         */
        float getTextureHeight();

        /**
         * @return The x size of the image file in pixels
         */
        int getSourceWidth();

        /**
         * @return The y size of the image file in pixels
         */
        int getSourceHeight();

        /**
         * @return The type of image this renders
         */
        ImageType getType();
    }

    /**
     * <p>An image provider for resource location. Normally used for local files but can work for any location able to be loaded into {@link TextureManager}.</p>
     *
     * @author Ocelot
     */
    private static class ResourceLocationImageProvider implements ImageProvider
    {
        private ResourceLocation location;
        private float u;
        private float v;
        private float textureWidth;
        private float textureHeight;
        private int sourceWidth;
        private int sourceHeight;

        private ResourceLocationImageProvider(CompoundNBT nbt)
        {
            this.deserializeNBT(nbt);
        }

        private ResourceLocationImageProvider(ResourceLocation location, float u, float v, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
        {
            this.location = location;
            this.u = u;
            this.v = v;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
        }

        @Nullable
        @Override
        public ResourceLocation getLocation()
        {
            return location;
        }

        @Override
        public float getU()
        {
            return u;
        }

        @Override
        public float getV()
        {
            return v;
        }

        @Override
        public float getTextureWidth()
        {
            return textureWidth;
        }

        @Override
        public float getTextureHeight()
        {
            return textureHeight;
        }

        @Override
        public int getSourceWidth()
        {
            return sourceWidth;
        }

        @Override
        public int getSourceHeight()
        {
            return sourceHeight;
        }

        @Override
        public ImageType getType()
        {
            return ImageType.RESOURCE_LOCATION;
        }

        @Override
        public void free()
        {
            Minecraft.getInstance().getTextureManager().deleteTexture(this.location);
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("location", this.location.toString());
            nbt.putFloat("u", this.u);
            nbt.putFloat("v", this.v);
            nbt.putFloat("textureWidth", this.textureWidth);
            nbt.putFloat("textureHeight", this.textureHeight);
            nbt.putFloat("sourceWidth", this.sourceWidth);
            nbt.putFloat("sourceHeight", this.sourceHeight);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            this.location = new ResourceLocation(nbt.getString("location"));
            this.u = nbt.getFloat("u");
            this.v = nbt.getFloat("v");
            this.textureWidth = nbt.getFloat("textureWidth");
            this.textureHeight = nbt.getFloat("textureHeight");
            this.sourceWidth = nbt.getInt("sourceWidth");
            this.sourceHeight = nbt.getInt("sourceHeight");
        }
    }

    /**
     * <p>An image provider for {@link IIcon}.</p>
     *
     * @author Ocelot
     * @see IIcon
     */
    private static class IconImageProvider implements ImageProvider
    {
        private IIcon icon;

        private IconImageProvider(CompoundNBT nbt)
        {
            this.deserializeNBT(nbt);
        }

        private IconImageProvider(IIcon icon)
        {
            this.icon = icon;
        }

        @Nullable
        @Override
        public ResourceLocation getLocation()
        {
            return this.icon.getIconLocation();
        }

        @Override
        public float getU()
        {
            return this.icon.getU();
        }

        @Override
        public float getV()
        {
            return this.icon.getV();
        }

        @Override
        public float getTextureWidth()
        {
            return this.icon.getWidth();
        }

        @Override
        public float getTextureHeight()
        {
            return this.icon.getHeight();
        }

        @Override
        public int getSourceWidth()
        {
            return this.icon.getSourceWidth();
        }

        @Override
        public int getSourceHeight()
        {
            return this.icon.getSourceHeight();
        }

        @Override
        public ImageType getType()
        {
            return ImageType.ICON;
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            return IIcon.serializeNBT(this.icon);
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            this.icon = IIcon.deserializeNBT(nbt);
        }
    }

    /**
     * <p>An image provider for online images.</p>
     *
     * @author Ocelot
     */
    private static class OnlineImageProvider implements ImageProvider
    {
        private String url;
        private ResourceLocation location;
        private float u;
        private float v;
        private float textureWidth;
        private float textureHeight;
        private int sourceWidth;
        private int sourceHeight;
        private long cacheTime;

        public OnlineImageProvider(CompoundNBT nbt)
        {
            this.deserializeNBT(nbt);
        }

        public OnlineImageProvider(String url, float u, float v, float textureWidth, float textureHeight, TimeUnit unit, long cacheTime)
        {
            this.url = url;
            this.location = null;
            this.u = u;
            this.v = v;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.sourceWidth = 1;
            this.sourceHeight = 1;
            this.cacheTime = unit.toMillis(cacheTime);
        }

        @Override
        public void request()
        {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> OnlineImageCache.request(this.url, TimeUnit.MILLISECONDS, this.cacheTime, (loc, cachedImage) ->
            {
                this.location = loc;
                if (this.textureWidth == -1)
                    this.textureWidth = cachedImage.getWidth();
                if (this.textureHeight == -1)
                    this.textureHeight = cachedImage.getHeight();
                this.sourceWidth = cachedImage.getWidth();
                this.sourceHeight = cachedImage.getHeight();
            }, null));
        }

        @Nullable
        @Override
        public ResourceLocation getLocation()
        {
            return location;
        }

        @Override
        public float getU()
        {
            return u;
        }

        @Override
        public float getV()
        {
            return v;
        }

        @Override
        public float getTextureWidth()
        {
            return textureWidth;
        }

        @Override
        public float getTextureHeight()
        {
            return textureHeight;
        }

        @Override
        public int getSourceWidth()
        {
            return sourceWidth;
        }

        @Override
        public int getSourceHeight()
        {
            return sourceHeight;
        }

        @Override
        public ImageType getType()
        {
            return ImageType.ONLINE;
        }

        @Override
        public void free()
        {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> OnlineImageCache.delete(this.url));
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("url", this.url);
            nbt.putFloat("u", this.u);
            nbt.putFloat("v", this.v);
            nbt.putFloat("textureWidth", this.textureWidth);
            nbt.putFloat("textureHeight", this.textureHeight);
            nbt.putFloat("sourceWidth", this.sourceWidth);
            nbt.putFloat("sourceHeight", this.sourceHeight);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            this.url = nbt.getString("url");
            this.u = nbt.getFloat("u");
            this.v = nbt.getFloat("v");
            this.textureWidth = nbt.getFloat("textureWidth");
            this.textureHeight = nbt.getFloat("textureHeight");
            this.sourceWidth = nbt.getInt("sourceWidth");
            this.sourceHeight = nbt.getInt("sourceHeight");
        }
    }

    /**
     * Creates a new {@link ResourceLocationImageProvider} with the specified local location, u, v, width, and height. The texture file size is automatically set to 256x256.
     *
     * @param location      The location of the texture file
     * @param u             The x on the texture to start
     * @param v             The y on the texture to start
     * @param textureWidth  The width of the selection to grab from the texture
     * @param textureHeight The height of the selection to grab from the texture
     * @return The image provider with the provided details
     */
    public static ImageProvider with(ResourceLocation location, float u, float v, float textureWidth, float textureHeight)
    {
        return new ImageComponent.ResourceLocationImageProvider(location, u, v, textureWidth, textureHeight, 256, 256);
    }

    /**
     * Creates a new {@link ResourceLocationImageProvider} with the specified local location, u, v, width, height, texture file width, and texture file height.
     *
     * @param location      The location of the texture file
     * @param u             The x on the texture to start
     * @param v             The y on the texture to start
     * @param textureWidth  The width of the selection to grab from the texture
     * @param textureHeight The height of the selection to grab from the texture
     * @param sourceWidth   The width of the texture file
     * @param sourceHeight  The height of the texture file
     * @return The image provider with the provided details
     */
    public static ImageProvider with(ResourceLocation location, float u, float v, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
    {
        return new ImageComponent.ResourceLocationImageProvider(location, u, v, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Creates a new {@link IconImageProvider} with the specified {@link IIcon}.
     *
     * @param icon The icon to use
     * @return The image provider with the provided details
     */
    public static ImageProvider with(IIcon icon)
    {
        return new IconImageProvider(icon);
    }

    /**
     * Creates a new {@link OnlineImageProvider} with the specified URL. The uv is set to zero and the selection size is set to the image size with no cache time specified.
     *
     * @param url The url of the image
     * @return The image provider with the provided details
     */
    public static ImageProvider with(String url)
    {
        return new ImageComponent.OnlineImageProvider(url, 0, 0, -1, -1, TimeUnit.MILLISECONDS, 0);
    }

    /**
     * Creates a new {@link OnlineImageProvider} with the specified URL and cache time. The uv is set to zero and the selection size is set to the image size.
     *
     * @param url       The url of the image
     * @param unit      The time unit the cache time is provided in
     * @param cacheTime The amount of the specified unit to cache the image for
     * @return The image provider with the provided details
     */
    public static ImageProvider with(String url, TimeUnit unit, long cacheTime)
    {
        return new ImageComponent.OnlineImageProvider(url, 0, 0, -1, -1, unit, cacheTime);
    }

    /**
     * Creates a new {@link OnlineImageProvider} with the specified URL u, v, texture width, and texture height. No cache time is specified.
     *
     * @param url           The url of the image
     * @param u             The x on the texture to start
     * @param v             The y on the texture to start
     * @param textureWidth  The width of the selection to grab from the texture
     * @param textureHeight The height of the selection to grab from the texture
     * @return The image provider with the provided details
     */
    public static ImageProvider with(String url, float u, float v, float textureWidth, float textureHeight)
    {
        return new ImageComponent.OnlineImageProvider(url, u, v, textureWidth, textureHeight, TimeUnit.MILLISECONDS, 0);
    }

    /**
     * Creates a new {@link OnlineImageProvider} with the specified URL u, v, texture width, texture height, and cache time.
     *
     * @param url           The url of the image
     * @param u             The x on the texture to start
     * @param v             The y on the texture to start
     * @param textureWidth  The width of the selection to grab from the texture
     * @param textureHeight The height of the selection to grab from the texture
     * @param unit          The time unit the cache time is provided in
     * @param cacheTime     The amount of the specified unit to cache the image for
     * @return The image provider with the provided details
     */
    public static ImageProvider with(String url, float u, float v, float textureWidth, float textureHeight, TimeUnit unit, long cacheTime)
    {
        return new ImageComponent.OnlineImageProvider(url, u, v, textureWidth, textureHeight, unit, cacheTime);
    }
}
