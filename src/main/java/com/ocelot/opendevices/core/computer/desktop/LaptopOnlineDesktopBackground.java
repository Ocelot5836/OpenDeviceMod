package com.ocelot.opendevices.core.computer.desktop;

import com.ocelot.opendevices.api.component.SpinnerComponent;
import com.ocelot.opendevices.api.computer.desktop.OnlineDesktopBackground;
import com.ocelot.opendevices.api.util.ImageFit;
import com.ocelot.opendevices.api.util.OnlineImageCache;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class LaptopOnlineDesktopBackground implements OnlineDesktopBackground
{
    private String url;
    private ResourceLocation location;
    private float u;
    private float v;
    private float width;
    private float height;
    private int imageWidth;
    private int imageHeight;
    private int loadedImageWidth;
    private int loadedImageHeight;
    private long cacheTime;
    private int progress;
    private ImageFit fit;

    public LaptopOnlineDesktopBackground(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    public LaptopOnlineDesktopBackground(String url, ImageFit fit)
    {
        this(url, 0, 0, -1, -1, -1, -1, TimeUnit.MILLISECONDS, 0, fit);
    }

    public LaptopOnlineDesktopBackground(String url, TimeUnit unit, long cacheTime, ImageFit fit)
    {
        this(url, 0, 0, -1, -1, -1, -1, unit, cacheTime, fit);
    }

    public LaptopOnlineDesktopBackground(String url, ImageFit fit, float u, float v, float width, float height)
    {
        this(url, u, v, width, height, -1, -1, TimeUnit.MILLISECONDS, 0, fit);
    }

    public LaptopOnlineDesktopBackground(String url, ImageFit fit, float u, float v, float width, float height, int imageWidth, int imageHeight)
    {
        this(url, u, v, width, height, imageWidth, imageHeight, TimeUnit.MILLISECONDS, 0, fit);
    }

    public LaptopOnlineDesktopBackground(String url, float u, float v, float width, float height, TimeUnit unit, long cacheTime, ImageFit fit)
    {
        this(url, u, v, width, height, -1, -1, unit, cacheTime, fit);
    }

    public LaptopOnlineDesktopBackground(String url, float u, float v, float width, float height, int imageWidth, int imageHeight, TimeUnit unit, long cacheTime, ImageFit fit)
    {
        this.url = url;
        this.location = null;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.loadedImageWidth = 1;
        this.loadedImageHeight = 1;
        this.cacheTime = unit.toMillis(cacheTime);
        this.fit = fit;
    }

    @Override
    public void update()
    {
        if (this.location != null)
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
    public void request()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> OnlineImageCache.request(this.url, TimeUnit.MILLISECONDS, this.cacheTime, (loc, cachedImage) ->
        {
            this.location = loc;
            this.loadedImageWidth = cachedImage.getWidth();
            this.loadedImageHeight = cachedImage.getHeight();
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
    public float getWidth()
    {
        return this.width == -1 ? this.getImageWidth() : this.width;
    }

    @Override
    public float getHeight()
    {
        return this.height == -1 ? this.getImageHeight() : this.height;
    }

    @Override
    public int getImageWidth()
    {
        return this.imageWidth != -1 ? this.imageWidth : this.loadedImageWidth;
    }

    @Override
    public int getImageHeight()
    {
        return this.imageHeight != -1 ? this.imageHeight : this.loadedImageHeight;
    }

    @Override
    public int getProgress()
    {
        return progress;
    }

    @Override
    public ImageFit getFit()
    {
        return fit;
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
        nbt.putFloat("width", this.width);
        nbt.putFloat("height", this.height);
        nbt.putInt("imageWidth", this.imageWidth);
        nbt.putInt("imageHeight", this.imageHeight);
        nbt.putLong("cacheTime", this.cacheTime);
        nbt.putByte("fit", this.fit.serialize());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.url = nbt.getString("url");
        this.u = nbt.getFloat("u");
        this.v = nbt.getFloat("v");
        this.width = nbt.getFloat("width");
        this.height = nbt.getFloat("height");
        this.imageWidth = nbt.getInt("imageWidth");
        this.imageHeight = nbt.getInt("imageHeight");
        this.cacheTime = nbt.getLong("cacheTime");
        this.fit = ImageFit.deserialize(nbt.getByte("fit"));
    }
}
