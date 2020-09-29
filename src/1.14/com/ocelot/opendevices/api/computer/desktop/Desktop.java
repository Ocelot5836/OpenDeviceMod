package com.ocelot.opendevices.api.computer.desktop;

import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.util.ImageFit;

import javax.annotation.Nullable;

/**
 * <p>Represents the most abstract form of a desktop for the {@link Computer}. Manages the {@link DesktopBackground} and desktop icons.</p>
 *
 * @author Ocelot
 * @see Computer
 * @see DesktopBackground
 */
public interface Desktop
{
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
