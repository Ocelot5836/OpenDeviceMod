package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.Laptop;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * <p>Handles {@link Window} management for the {@link Laptop}.</p>
 *
 * @author Ocelot
 * @see Laptop
 */
public interface WindowManager
{
    /**
     * Opens a new window under the specified process.
     *
     * @param processId The process opening the window
     * @return The id of the window created or null if there was an error
     */
    @Nullable
    UUID openWindow(UUID processId);

    /**
     * Focuses the window with the specified ID and moves it to the front.
     *
     * @param windowId The id of the window to focus or null to unfocus
     */
    void focusWindow(@Nullable UUID windowId);

    /**
     * Closes the windows with the specified ids.
     *
     * @param windowIds The ids of the windows to close
     */
    void closeWindows(Collection<UUID> windowIds);

    /**
     * Closes the windows with the specified ids.
     *
     * @param windowIds The ids of the windows to close
     */
    void closeWindows(UUID... windowIds);

    /**
     * Moves the window with the specified id in the specified direction.
     *
     * @param windowId The id of the window to move
     * @param dx       The amount in the x direction to move
     * @param dy       The amount in the y direction to move
     */
    void moveWindow(UUID windowId, float dx, float dy);

    /**
     * Sets the position of the window with the specified id to the specified coordinates.
     *
     * @param windowId The id of the window to move
     * @param x        The new x position of the window
     * @param y        The new y position of the window
     */
    void setWindowPosition(UUID windowId, float x, float y);

    /**
     * Sets the size of the window with the specified id to the specified width and height.
     *
     * @param windowId The id of the window to change
     * @param width    The new x size of the window
     * @param height   The new y size of the window
     */
    void setWindowSize(UUID windowId, int width, int height);

    /**
     * Checks the opened windows for the window with the specified id.
     *
     * @param windowId The id of the window to fetch
     * @return The window found or null if no window has that id
     */
    @Nullable
    Window getWindow(UUID windowId);

    /**
     * @return All currently opened windows
     */
    Window[] getWindows();

    /**
     * @return The currently focused window or null if there is no window focused
     */
    @Nullable
    Window getFocusedWindow();

    /**
     * @return The window rendering with the highest priority over others or null if there are no windows opened
     */
    @Nullable
    Window getTopWindow();

    /**
     * @return The id of the currently focused window or null if there is no window focused
     */
    @Nullable
    UUID getFocusedWindowId();

    /**
     * @return The id of the window rendering with the highest priority over others or null if there are no windows opened
     */
    @Nullable
    UUID getTopWindowId();
}
