package com.ocelot.opendevices.core.computer.desktop;

import com.ocelot.opendevices.api.component.SpinnerComponent;
import com.ocelot.opendevices.api.computer.desktop.OnlineDesktopBackground;
import com.ocelot.opendevices.api.util.OnlineImageCache;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;

public class LaptopOnlineDesktopBackground implements OnlineDesktopBackground
{
    private String url;
    private ResourceLocation location;
    private long cacheTime;
    private int progress;

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
        this.cacheTime = cacheTime;
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
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> OnlineImageCache.request(this.url, this.cacheTime, loc -> this.location = loc, null));
    }

    @Nullable
    @Override
    public ResourceLocation getLocation()
    {
        return location;
    }

    @Override
    public int getProgress()
    {
        return progress;
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
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> OnlineImageCache.delete(this.url));
    }
}
