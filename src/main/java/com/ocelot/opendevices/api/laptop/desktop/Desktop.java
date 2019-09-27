package com.ocelot.opendevices.api.laptop.desktop;

import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.core.window.Window;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Represents the most abstract form of a desktop for the {@link Laptop}. Manages the {@link DesktopBackground}, desktop icons, and applications.</p>
 *
 * @author Ocelot
 * @see Laptop
 */
public interface Desktop
{
    //    /**
    //     * Opens the application with the specified information.
    //     *
    //     * @param info The information to open the app of
    //     */
    //    void openApplication(AppInfo info);
    //
    //    /**
    //     * Closes the application with the specified information.
    //     *
    //     * @param info The information to close the app of
    //     */
    //    void closeApplication(AppInfo info);

    /**
     * Focuses the window with the specified ID and moves it to the front.
     *
     * @param windowId The id of the window to focus or null to unfocus
     */
    void focusWindow(@Nullable UUID windowId);

    /**
     * Closes all the windows that are currently open.
     */
    void closeAllWindows();

    /**
     * Closes the specified window.
     *
     * @param windowId The id of the window to close
     */
    void closeWindow(UUID windowId);

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
    default Window getFocusedWindow()
    {
        return this.getWindow(this.getFocusedWindowId());
    }

    /**
     * @return The id of the currently focused window or null if there is no window focused
     */
    @Nullable
    UUID getFocusedWindowId();

    /**
     * @return The current desktop background
     */
    DesktopBackground getBackground();

    /**
     * Sets the current desktop background.
     *
     * @param background The new background to use
     */
    void setBackground(@Nullable DesktopBackground background);
}
