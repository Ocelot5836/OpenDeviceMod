package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.window.Window;

import javax.annotation.Nullable;

/**
 * <p>A basic implementation of {@link ProcessInputHandler} that handles input for {@link Application} layouts.</p>
 *
 * @param <D> The device the process is being used for
 * @param <T> The type of process this renderer uses
 * @author Ocelot
 * @see ProcessInputHandler
 * @see Application
 * @see DeviceProcess
 */
public class ApplicationInputHandler<D extends Device, T extends DeviceProcess<D> & Application> implements ProcessInputHandler<D, T>
{
    @Override
    public boolean onKeyPressed(T process, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onKeyPressed(keyCode, scanCode, mods);
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(T process, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onKeyReleased(keyCode, scanCode, mods);
        }
        return false;
    }

    @Override
    public boolean onMousePressed(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onMousePressed(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onMouseReleased(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean onMouseScrolled(T process, @Nullable Window window, double mouseX, double mouseY, double amount)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onMouseScrolled(mouseX, mouseY, amount);
        }
        return false;
    }

    @Override
    public void onMouseMoved(T process, @Nullable Window window, double mouseX, double mouseY)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            if (layout != null)
            {
                layout.onMouseMoved(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean onMouseDragged(T process, @Nullable Window window, double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (window != null)
        {
            Layout layout = process.getLayout(window.getId());
            return layout != null && layout.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
        }
        return false;
    }
}
