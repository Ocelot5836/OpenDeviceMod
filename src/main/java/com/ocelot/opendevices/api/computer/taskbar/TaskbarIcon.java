package com.ocelot.opendevices.api.computer.taskbar;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * <p>An icon that can be clicked and rendered on the task bar.</p>
 *
 * @author Ocelot
 */
public interface TaskbarIcon extends INBTSerializable<CompoundNBT>
{
    /**
     * Called when this icon is activated.
     */
    boolean execute();

    /**
     * @return The title of the window
     */
    String getName();

    /**
     * @return The icon of this icon or null for no custom icon
     */
    @Nullable
    ResourceLocation getIconSprite();

    /**
     * @return Whether or not a box should be rendered around this icon
     */
    boolean isActive();

    /**
     * @return The type of task bar icon this is
     */
    TaskbarIconType getType();
}
