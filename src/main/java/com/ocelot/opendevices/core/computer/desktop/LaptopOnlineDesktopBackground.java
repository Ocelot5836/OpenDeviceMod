package com.ocelot.opendevices.core.computer.desktop;

import com.ocelot.opendevices.api.computer.desktop.OnlineDesktopBackground;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;

public class LaptopOnlineDesktopBackground implements OnlineDesktopBackground
{
    private String url;
    private ResourceLocation location;
    private int width;
    private int height;
    private long cacheTime;
    private long expires;

    public LaptopOnlineDesktopBackground(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    public LaptopOnlineDesktopBackground(String url)
    {
        this(url, 0);
    }

    public LaptopOnlineDesktopBackground(String url, long cacheTime)
    {
        this.url = url;
        this.location = null;
        this.width = -1;
        this.height = -1;
        this.cacheTime = cacheTime;
        this.expires = -1;
    }

    @Override
    public void request()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            // TODO tell cache to load image
        });
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Nullable
    @Override
    public ResourceLocation getLocation()
    {
        return location;
    }

    @Override
    public float getWidth()
    {
        return width;
    }

    @Override
    public float getHeight()
    {
        return height;
    }

    @Override
    public long getExpirationTime()
    {
        return expires;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("url", this.url);
        nbt.putLong("cacheTime", this.cacheTime);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.url = nbt.getString("url");
        this.cacheTime = nbt.getLong("cacheTime");
    }

    @Override
    public void free()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            // TODO tell cache to erase local
        });
    }
}
