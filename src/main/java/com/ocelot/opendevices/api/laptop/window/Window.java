package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;

import java.util.UUID;

/**
 * <p>The most abstract form of a Window. This contains all the required functionality for the API.</p>
 * <p>A window is a box that can display onto a {@link Laptop}. The window has the ability to be moved and render it's content.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public interface Window
{
    /**
     * Moves this window in the specified direction.
     *
     * @param xDirection The amount in the x to move
     * @param yDirection The amount in the y to move
     * @deprecated Setters aren't going to exist anymore
     */
    default void move(float xDirection, float yDirection) {}

    /**
     * Centers this window on the desktop of the laptop.
     *
     * @deprecated Setters aren't going to exist anymore
     */
    default void center()
    {
        this.setPosition((DeviceConstants.LAPTOP_SCREEN_WIDTH - this.getWidth()) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.getLaptop().getTaskBar().getHeight() - (this.getHeight() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f);
    }

    /**
     * @return The laptop this window is opened inside of
     */
    Laptop getLaptop();

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
     * @return The x size of this window including the borders
     */
    int getWidth();

    /**
     * @return The y size of this window including the borders
     */
    int getHeight();

    /**
     * Moves this window to the specified position.
     *
     * @param x The new x position of the window
     * @param y The new y position of the window
     * @deprecated Setters aren't going to exist anymore
     */
    default void setPosition(float x, float y) {}

    /**
     * Sets the size of this window to the specified values.
     *
     * @param width  The new x size of the window
     * @param height The new y size of the window
     * @deprecated Setters aren't going to exist anymore
     */
    default void setSize(int width, int height) {}
}
