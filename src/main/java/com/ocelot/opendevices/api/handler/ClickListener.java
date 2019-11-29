package com.ocelot.opendevices.api.handler;

/**
 * <p>Used to detect when the mouse is pressed.</p>
 *
 * @author Ocelot
 */
public interface ClickListener
{
    /**
     * Handles when the mouse is pressed.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button on the mouse pressed
     */
    void handle(double mouseX, double mouseY, int mouseButton);
}
