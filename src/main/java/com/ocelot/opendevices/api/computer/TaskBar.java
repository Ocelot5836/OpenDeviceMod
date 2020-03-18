package com.ocelot.opendevices.api.computer;

import com.ocelot.opendevices.api.computer.window.Window;

/**
 * <p>Represents the most abstract form of a task bar for the {@link Computer}. Displays information such as tray items, pinned apps, etc.</p>
 *
 * @author Ocelot
 * @see Computer
 */
public interface TaskBar
{
    /**
     * @return Whether or not the task bar is enlarged
     */
    boolean isEnlarged();

    /**
     * @return The windows that are currently displayed on the task bar
     */
    Window[] getDisplayedWindows();

    /**
     * @return The height of the task bar
     */
    default int getHeight()
    {
        return this.isEnlarged() ? 24 : 16;
    }
}
