package com.ocelot.opendevices.api.device.laptop.desktop;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class LaptopDesktopBackground implements INBTSerializable<CompoundNBT>
{
    private ResourceLocation location;
    private String url;
    private boolean online;
    private float u;
    private float v;
    private float width;
    private float height;
    private int imageWidth;
    private int imageHeight;

    public LaptopDesktopBackground(ResourceLocation location, float u, float v, float width, float height, int imageWidth, int imageHeight)
    {
        this.location = location;
        this.url = null;
        this.online = false;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public LaptopDesktopBackground(String url, float u, float v, float width, float height)
    {
        this.location = null;
        this.url = url;
        this.online = true;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = 0;
        this.imageHeight = 0;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        if (!this.online)
        {
            nbt.putString("location", this.location.toString());
        }
        else
        {
            nbt.putString("url", this.url);
        }
        nbt.putBoolean("online", this.online);
        nbt.putFloat("u", this.u);
        nbt.putFloat("v", this.v);
        nbt.putFloat("width", this.width);
        nbt.putFloat("height", this.height);
        nbt.putInt("imageWidth", this.imageWidth);
        nbt.putInt("imageHeight", this.imageHeight);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        boolean online = nbt.getBoolean("online");
        this.location = !online ? new ResourceLocation(nbt.getString("location")) : null;
        this.url = online ? nbt.getString("url") : null;
        this.u = nbt.getFloat("u");
        this.v = nbt.getFloat("v");
        this.width = nbt.getFloat("width");
        this.height = nbt.getFloat("height");
        this.imageWidth = nbt.getInt("imageWidth");
        this.imageHeight = nbt.getInt("imageHeight");
    }

    public boolean isOnline()
    {
        return online;
    }

    @Nullable
    public ResourceLocation getLocation()
    {
        return location;
    }

    @Nullable
    public String getUrl()
    {
        return url;
    }

    public float getU()
    {
        return u;
    }

    public float getV()
    {
        return v;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public int getImageWidth()
    {
        return imageWidth;
    }

    public int getImageHeight()
    {
        return imageHeight;
    }

    public static LaptopDesktopBackground createDefault()
    {
        return new LaptopDesktopBackground(Constants.DEFAULT_BACKGROUND_LOCATION, 0, 0, Constants.LAPTOP_DEVICE_WIDTH / 2f, Constants.LAPTOP_DEVICE_HEIGHT / 2f, Constants.LAPTOP_DEVICE_WIDTH / 2, Constants.LAPTOP_DEVICE_HEIGHT / 2);
    }
}
