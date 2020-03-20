package com.ocelot.opendevices.core.computer.desktop;

import com.ocelot.opendevices.api.computer.desktop.LocalDesktopBackground;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class LaptopLocalDesktopBackground implements LocalDesktopBackground
{
    private ResourceLocation location;
    private float u;
    private float v;
    private float width;
    private float height;
    private int imageWidth;
    private int imageHeight;

    public LaptopLocalDesktopBackground(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    public LaptopLocalDesktopBackground(ResourceLocation location, float u, float v, float width, float height, int imageWidth, int imageHeight)
    {
        this.location = location;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public ResourceLocation getLocation()
    {
        return location;
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

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("location", this.location.toString());
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
        this.location = new ResourceLocation(nbt.getString("location"));
        this.u = nbt.getFloat("u");
        this.v = nbt.getFloat("v");
        this.width = nbt.getFloat("width");
        this.height = nbt.getFloat("height");
        this.imageWidth = nbt.getInt("imageWidth");
        this.imageHeight = nbt.getInt("imageHeight");
    }
}
