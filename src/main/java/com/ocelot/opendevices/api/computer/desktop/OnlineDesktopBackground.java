package com.ocelot.opendevices.api.computer.desktop;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>A {@link DesktopBackground} using an online image.</p>
 *
 * @author Ocelot
 */
public interface OnlineDesktopBackground extends DesktopBackground
{
    @Nullable
    @Override
    ResourceLocation getLocation();

    /**
     * Requests this image from URL if not already requesting.
     */
    void request();

    /**
     * @return The progress of the spinner
     */
    int getProgress();

    @Override
    default DesktopBackgroundType getType()
    {
        return DesktopBackgroundType.ONLINE;
    }
}
