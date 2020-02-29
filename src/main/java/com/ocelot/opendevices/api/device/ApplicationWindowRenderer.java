package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.util.TooltipRenderer;

/**
 * <p>A basic implementation of {@link ProcessWindowRenderer} that renders {@link Application} layouts.</p>
 *
 * @param <D> The device the process is being used for
 * @param <T> The type of process this renderer uses
 * @author Ocelot
 * @see ProcessWindowRenderer
 * @see Application
 * @see DeviceProcess
 */
public class ApplicationWindowRenderer<D extends Device, T extends DeviceProcess<D> & Application> implements ProcessWindowRenderer<D, T>
{
    @Override
    public void render(T application, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Layout layout = application.getLayout(window.getId());
        if (layout != null)
        {
            float contentX = window.getInterpolatedX(partialTicks) + 1;
            float contentY = window.getInterpolatedY(partialTicks) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            layout.render(posX + contentX, posY + contentY, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, T application, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Layout layout = application.getLayout(window.getId());
        if (layout != null)
        {
            float contentX = window.getInterpolatedX(partialTicks) + 1;
            float contentY = window.getInterpolatedY(partialTicks) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            layout.renderOverlay(renderer, posX + contentX, posY + contentY, mouseX, mouseY, partialTicks);
        }
    }
}
