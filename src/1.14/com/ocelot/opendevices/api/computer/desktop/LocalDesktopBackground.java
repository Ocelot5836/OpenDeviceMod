package com.ocelot.opendevices.api.computer.desktop;

import net.minecraft.util.ResourceLocation;

/**
 * <p>A {@link DesktopBackground} using a {@link ResourceLocation}.</p>
 *
 * @author Ocelot
 */
public interface LocalDesktopBackground extends DesktopBackground
{
    @Override
    default DesktopBackgroundType getType()
    {
        return DesktopBackgroundType.RESOURCE_LOCATION;
    }

    @Override
    default void free()
    {
    }
}
