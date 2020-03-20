package com.ocelot.opendevices.api.computer.desktop;

import net.minecraft.util.ResourceLocation;

/**
 * <p>A {@link DesktopBackground} using a {@link ResourceLocation}.</p>
 *
 * @author Ocelot
 */
public interface LocalDesktopBackground extends DesktopBackground
{
    /**
     * @return The location of the desktop background texture
     */
    ResourceLocation getLocation();

    /**
     * @return The x position of the texture
     */
    float getU();

    /**
     * @return The y position of the texture
     */
    float getV();

    /**
     * @return The width of the texture
     */
    float getWidth();

    /**
     * @return The height of the texture
     */
    float getHeight();

    /**
     * @return The width of the image file
     */
    int getImageWidth();

    /**
     * @return The height of the image file
     */
    int getImageHeight();

    @Override
    default DesktopBackgroundType getType()
    {
        return DesktopBackgroundType.RESOURCE_LOCATION;
    }
}
