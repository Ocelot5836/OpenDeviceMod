package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * <p>The most abstract form of a Window. This contains all the required functionality for the API.</p>
 * <p>A window is a box that can display onto a {@link Laptop}. The window has the ability to be moved and render it's content.</p>
 * <p>Some methods, such as {@link Window#focus()} or {@link Window#close()}, can be called from {@link Laptop}, but are here for ease of access.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public interface Window
{
    /**
     * Sets this window to be the main, focused window.
     */
    void focus();

    /**
     * Closes this window.
     */
    void close();

    /**
     * Moves this window in the specified direction.
     *
     * @param xDirection The amount in the x to move
     * @param yDirection The amount in the y to move
     */
    void move(float xDirection, float yDirection);

    /**
     * @return The id of this window. Used for Client/Server synchronization
     */
    UUID getId();

    /**
     * @return The type of content inside this window
     */
    WindowContentType getContentType();

    /**
     * @return The registry name of the content inside this window
     */
    ResourceLocation getContentId();

    /**
     * @return The laptop this window is opened inside of
     */
    Laptop getLaptop();

    /**
     * @return The x position of this window
     */
    float getX();

    /**
     * @return The y position of this window
     */
    float getY();

    /**
     * @return The x size of this window
     */
    int getWidth();

    /**
     * @return The y size of this window
     */
    int getHeight();

    /**
     * Centers this window on the desktop of the laptop.
     */
    void center();

    /**
     * Moves this window to the specified position.
     *
     * @param x The new x position of the window
     * @param y The new y position of the window
     */
    void setPosition(float x, float y);

    /**
     * Sets the size of this window to the specified values.
     *
     * @param width  The new x size of the window
     * @param height The new y size of the window
     */
    void setSize(int width, int height);
}
