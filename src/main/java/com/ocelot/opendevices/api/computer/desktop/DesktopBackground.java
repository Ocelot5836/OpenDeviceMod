package com.ocelot.opendevices.api.computer.desktop;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.core.LaptopDesktop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

/**
 * <p>An image that can be rendered onto the back of the {@link LaptopDesktop}.</p>
 * <p>Supports {@link ResourceLocation} and a URL string to get the image source.</p>
 *
 * @author Ocelot
 * @see LaptopDesktop
 * @deprecated TODO update to use {@link IForgeRegistry} so it syncs with the server. Limit the valid backgrounds to some registry
 */
public class DesktopBackground implements INBTSerializable<CompoundNBT>
{
    public static final DesktopBackground DEFAULT = new DesktopBackground(DeviceConstants.DEFAULT_BACKGROUND_LOCATION, 0, 0, DeviceConstants.LAPTOP_GUI_WIDTH / 2f, DeviceConstants.LAPTOP_GUI_HEIGHT / 2f, DeviceConstants.LAPTOP_GUI_WIDTH / 2, DeviceConstants.LAPTOP_GUI_HEIGHT / 2);

    private ResourceLocation location;
    private String url;
    private boolean online;
    private float u;
    private float v;
    private float width;
    private float height;
    private int imageWidth;
    private int imageHeight;

    public DesktopBackground(ResourceLocation location, float u, float v, float width, float height, int imageWidth, int imageHeight)
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

    public DesktopBackground(String url, float u, float v, float width, float height)
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

    /**
     * @return An exact copy of this background
     */
    public DesktopBackground copy()
    {
        return this.online ? new DesktopBackground(this.url, this.u, this.v, this.width, this.height) : new DesktopBackground(this.location, this.u, this.v, this.width, this.height, this.imageWidth, this.imageHeight);
    }

    /**
     * @return Whether or not this background is an online image
     */
    public boolean isOnline()
    {
        return online;
    }

    /**
     * @return The location of the background. Returns null if this is an online image.
     */
    @Nullable
    public ResourceLocation getLocation()
    {
        return location;
    }

    /**
     * @return The location of the background. Returns null if this is not an online image.
     */
    @Nullable
    public String getUrl()
    {
        return url;
    }

    /**
     * @return The x position on the texture to start
     */
    public float getU()
    {
        return u;
    }

    /**
     * @return The y position on the texture to start
     */
    public float getV()
    {
        return v;
    }

    /**
     * @return The x size on the texture to go to
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * @return The y size on the texture to go to
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * @return The width of the entire image
     */
    public int getImageWidth()
    {
        return imageWidth;
    }

    /**
     * @return The height of the entire image
     */
    public int getImageHeight()
    {
        return imageHeight;
    }
}
