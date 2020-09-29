package com.ocelot.opendevices.api.computer.desktop;

import com.ocelot.opendevices.api.util.ImageFit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * <p>An image that is rendered on the back of the desktop.</p>
 *
 * @author Ocelot
 * @see DesktopBackgroundType
 */
public interface DesktopBackground extends INBTSerializable<CompoundNBT>
{
    /**
     * Called every tick to update the background.
     */
    default void update()
    {
    }

    /**
     * Frees resources used by this background.
     */
    default void free()
    {
    }

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

    /**
     * @return The type of background this is
     */
    DesktopBackgroundType getType();

    /**
     * @return How this background will fit to the screen
     */
    ImageFit getFit();
}
