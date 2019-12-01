package com.ocelot.opendevices.api.handler;

/**
 * <p>Used to detect when the mouse is pressed.</p>
 *
 * @param <T> The type of component returned in {@link #handle(Object, double, double, int)}
 * @author Ocelot
 */
public interface ComponentClickListener<T>
{
    /**
     * Handles when the mouse is pressed on a component.
     *
     * @param component   The component pressed
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button on the mouse pressed
     * @return Whether or not the action was handled
     */
    boolean handle(T component, double mouseX, double mouseY, int mouseButton);
}
