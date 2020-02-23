package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.Computer;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;

import java.util.UUID;

/**
 * <p>The most abstract form of a Window. This contains all the required functionality for the API.</p>
 * <p>A window is a box that can display onto a {@link Computer}. The window has the ability to be moved and render it's content.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public interface Window
{
    //    /**
    //     * Centers this window on the desktop of the laptop.
    //     *
    //     * @deprecated Setters aren't going to exist anymore
    //     */
    //    default void center()
    //    {
    //        this.setPosition((DeviceConstants.LAPTOP_SCREEN_WIDTH - this.getWidth()) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.getLaptop().getTaskBar().getHeight() - (this.getHeight() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f);
    //    }

    /**
     * @return The laptop this window is opened inside of
     */
    Computer getComputer();

    /**
     * @return The id of the running this window
     */
    UUID getProcessId();

    /**
     * @return The id of this window. Used for Client/Server synchronization
     */
    UUID getId();

    /**
     * @return The x position of this window
     */
    float getX();

    /**
     * @return The y position of this window
     */
    float getY();

    /**
     * @return The last x position of this window
     */
    float getLastX();

    /**
     * @return The last y position of this window
     */
    float getLastY();

    /**
     * @return The x size of this window including the borders
     */
    int getWidth();

    /**
     * @return The y size of this window including the borders
     */
    int getHeight();
}
