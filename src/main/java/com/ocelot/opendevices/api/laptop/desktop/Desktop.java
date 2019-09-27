package com.ocelot.opendevices.api.laptop.desktop;

import com.ocelot.opendevices.api.laptop.Laptop;

import javax.annotation.Nullable;

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
