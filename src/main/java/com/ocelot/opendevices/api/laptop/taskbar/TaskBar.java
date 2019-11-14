package com.ocelot.opendevices.api.laptop.taskbar;

import com.ocelot.opendevices.api.laptop.Laptop;

/**
 * <p>Represents the most abstract form of a task bar for the {@link Laptop}. Displays information such as tray items, pinned apps, etc.</p>
 *
 * @author Ocelot
 * @see Laptop
 */
public interface TaskBar
{
    /**
     * @return Whether or not the task bar is enlarged
     */
    boolean isEnlarged();

    /**
     * @return The height of the task bar
     */
    default int getHeight()
    {
        return this.isEnlarged() ? 24 : 16;
    }
}
