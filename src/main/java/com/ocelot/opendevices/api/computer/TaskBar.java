package com.ocelot.opendevices.api.computer;

/**
 * <p>Represents the most abstract form of a task bar for a {@link Computer}. Displays information such as tray items, pinned apps, etc.</p>
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
     * @return The icons displayed on the task bar
     */
    TaskbarIcon[] getDisplayedIcons();

    /**
     * @return The icons displayed in the tray
     */
    TrayIcon[] getTrayIcons();

    /**
     * @return The height of the task bar
     */
    default int getHeight()
    {
        return this.isEnlarged() ? 24 : 16;
    }
}
