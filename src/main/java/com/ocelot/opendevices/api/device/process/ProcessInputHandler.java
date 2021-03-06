package com.ocelot.opendevices.api.device.process;

import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.device.Device;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

/**
 * <p>Allows the listening of input events for processes. This also allows the listening of window inputs which can be used in any way. Should only be bound on the client side!</p>
 *
 * @param <D> The device the process is being used for
 * @param <T> The type of process this input handler uses
 * @author Ocelot
 * @see DeviceProcess
 */
public interface ProcessInputHandler<D extends Device, T extends DeviceProcess<D>>
{
    /**
     * Called when a key is pressed on the keyboard for a specific window or null if there is no window selected.
     *
     * @param process  The process this key was pressed for
     * @param window The window the key was pressed for
     * @param keyCode  The id of the key pressed or {@link GLFW#GLFW_KEY_UNKNOWN} if the key does not have a key token
     * @param scanCode A unique id for each key regardless of key tokens, but is platform-specific
     * @param mods     The modifier key flags
     * @return Whether or not this event was consumed
     */
    default boolean onKeyPressed(T process, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Called when a key is released on the keyboard for a specific window or null if there is no window selected.
     *
     * @param process  The process this key was pressed for
     * @param window The window the key was released for
     * @param keyCode  The id of the key released or {@link GLFW#GLFW_KEY_UNKNOWN} if the key does not have a key token
     * @param scanCode A unique id for each key regardless of key tokens, but is platform-specific
     * @param mods     The modifier key flags
     * @return Whether or not this event was consumed
     */
    default boolean onKeyReleased(T process, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Called when a button is pressed on the mouse for a specific window or null if there is no window selected.
     *
     * @param process     The process the mouse was pressed for
     * @param window    The window the mouse was pressed for
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed on the mouse
     * @return Whether or not this event was consumed
     */
    default boolean onMousePressed(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    /**
     * Called when a button is released on the mouse for a specific window or null if there is no window selected.
     *
     * @param process     The process the mouse was released for
     * @param window    The window the mouse was released for
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button released on the mouse
     * @return Whether or not this event was consumed
     */
    default boolean onMouseReleased(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    /**
     * Called when the mouse wheel of the mouse is scrolled for a specific window or null if there is no window selected.
     *
     * @param process  The process the mouse wheel was scrolled for
     * @param window The window the mouse wheel was scrolled for
     * @param mouseX   The x position of the mouse
     * @param mouseY   The y position of the mouse
     * @param amount   The amount the mouse wheel was scrolled
     * @return Whether or not this event was consumed
     */
    default boolean onMouseScrolled(T process, @Nullable Window window, double mouseX, double mouseY, double amount)
    {
        return false;
    }

    /**
     * Called when the mouse is moved for a specific window or null if there is no window selected.
     *
     * @param process  The process the mouse was moved for
     * @param window The window the mouse was moved for
     * @param mouseX   The x position of the mouse
     * @param mouseY   The y position of the mouse
     */
    default void onMouseMoved(T process, @Nullable Window window, double mouseX, double mouseY)
    {
    }

    /**
     * Called when the mouse is moved while a button is held for a specific window or null if there is no window selected.
     *
     * @param process     The process the mouse was dragged for
     * @param window    The window the mouse was dragged for
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed on the mouse while moving
     * @param deltaX      The amount in the x direction the mouse has moved
     * @param deltaY      The amount in the y direction the mouse has moved
     * @return Whether or not this event was consumed
     */
    default boolean onMouseDragged(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return false;
    }
}
