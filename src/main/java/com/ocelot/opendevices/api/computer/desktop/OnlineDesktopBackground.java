package com.ocelot.opendevices.api.computer.desktop;

import com.ocelot.opendevices.api.component.SpinnerComponent;
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
     * @return The location of the desktop background texture
     */
    @Nullable
    ResourceLocation getLocation();

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
