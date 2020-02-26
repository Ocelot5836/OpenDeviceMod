package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.TooltipRenderer;

/**
 * <p>Allows the rendering of data for windows attached to processes. Should only be bound on the client side!</p>
 *
 * @param <D> The device the process is being used for
 * @param <T> The type of process this renderer uses
 * @author Ocelot
 * @see DeviceProcess
 */
public interface ProcessWindowRenderer<D extends Device, T extends DeviceProcess<D>>
{
    /**
     * Renders the contents of the specified window.
     *
     * @param process      The process the window belongs to
     * @param window       The window being rendered
     * @param posX         The x position of the desktop
     * @param posY         The y position of the desktop
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    void render(T process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks);

    /**
     * Renders the overlay contents of the specified window.
     *
     * @param renderer     The rendering instance used for rendering tooltips
     * @param process      The process the window belongs to
     * @param window       The window being rendered
     * @param posX         The x position of the desktop
     * @param posY         The y position of the desktop
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    void renderOverlay(TooltipRenderer renderer, T process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks);
}
