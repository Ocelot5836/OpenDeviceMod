package com.ocelot.opendevices.api.computer.desktop;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.system.NativeResource;

import javax.annotation.Nullable;

/**
 * <p>A {@link DesktopBackground} using an online image.</p>
 *
 * @author Ocelot
 */
public interface OnlineDesktopBackground extends DesktopBackground, NativeResource
{
    /**
     * Requests this image from URL if not already requesting.
     */
    void request();

    /**
     * @return The URL of the desktop background texture
     */
    String getUrl();

    /**
     * @return The location of the desktop background texture
     */
    @Nullable
    ResourceLocation getLocation();

    /**
     * @return The width of the texture
     */
    float getWidth();

    /**
     * @return The height of the texture
     */
    float getHeight();

    /**
     * @return The time when this image will next expire
     */
    long getExpirationTime();

    @Override
    default DesktopBackgroundType getType()
    {
        return DesktopBackgroundType.ONLINE;
    }
}
