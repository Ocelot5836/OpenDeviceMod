package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;

import java.util.UUID;

/**
 * <p>The most abstract form of desktop-displayable content. This contains all the required functionality for the API.</p>
 * <p>This can display onto a {@link Laptop} and be rendered.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public interface DesktopContent
{
    /**
     * Notifies this content that it needs to be saved in the future.
     */
    void markDirty();

    /**
     * Syncs the data with the server and notifies all clients of the change.
     */
    void sync();

    /**
     * @return The id of this window. Used for Client/Server synchronization
     */
    UUID getId();

    /**
     * @return The laptop this window is opened inside of
     */
    Laptop getLaptop();

    /**
     * @return Whether or not this content is receiving input events
     */
    boolean isFocused();

    /**
     * @return Whether or not this content is the highest priority for rendering
     */
    boolean isTop();
}
