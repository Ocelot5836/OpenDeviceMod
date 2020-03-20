package com.ocelot.opendevices.api.computer.desktop;

import net.minecraft.nbt.CompoundNBT;
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
     * @return The type of background this is
     */
    DesktopBackgroundType getType();
}
