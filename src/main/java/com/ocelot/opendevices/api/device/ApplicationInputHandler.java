package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.application.Application;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.device.process.ProcessInputHandler;
import net.minecraft.client.Minecraft;

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
public class ApplicationInputHandler<D extends Device, T extends Application<D>> implements ProcessInputHandler<D, T>
{
    @Override
    public boolean onKeyPressed(T application, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        if (window != null)
        {
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onKeyPressed(keyCode, scanCode, mods);
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(T application, @Nullable Window window, int keyCode, int scanCode, int mods)
    {
        if (window != null)
        {
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onKeyReleased(keyCode, scanCode, mods);
        }
        return false;
    }

    @Override
    public boolean onMousePressed(T application, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        if (window != null)
        {
            float windowX = window.getInterpolatedX(Minecraft.getInstance().getRenderPartialTicks()) + 1;
            float windowY = window.getInterpolatedY(Minecraft.getInstance().getRenderPartialTicks()) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onMousePressed(mouseX - windowX, mouseY - windowY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(T application, @Nullable Window window, double mouseX, double mouseY, int mouseButton)
    {
        if (window != null)
        {
            float windowX = window.getInterpolatedX(Minecraft.getInstance().getRenderPartialTicks()) + 1;
            float windowY = window.getInterpolatedY(Minecraft.getInstance().getRenderPartialTicks()) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onMouseReleased(mouseX - windowX, mouseY - windowY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean onMouseScrolled(T application, @Nullable Window window, double mouseX, double mouseY, double amount)
    {
        if (window != null)
        {
            float windowX = window.getInterpolatedX(Minecraft.getInstance().getRenderPartialTicks()) + 1;
            float windowY = window.getInterpolatedY(Minecraft.getInstance().getRenderPartialTicks()) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onMouseScrolled(mouseX - windowX, mouseY - windowY, amount);
        }
        return false;
    }

    @Override
    public void onMouseMoved(T application, @Nullable Window window, double mouseX, double mouseY)
    {
        if (window != null)
        {
            float windowX = window.getInterpolatedX(Minecraft.getInstance().getRenderPartialTicks()) + 1;
            float windowY = window.getInterpolatedY(Minecraft.getInstance().getRenderPartialTicks()) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            Layout layout = application.getLayout(window.getId());
            if (layout != null)
            {
                layout.onMouseMoved(mouseX - windowX, mouseY - windowY);
            }
        }
    }

    @Override
    public boolean onMouseDragged(T application, @Nullable Window window, double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (window != null)
        {
            float windowX = window.getInterpolatedX(Minecraft.getInstance().getRenderPartialTicks()) + 1;
            float windowY = window.getInterpolatedY(Minecraft.getInstance().getRenderPartialTicks()) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
            Layout layout = application.getLayout(window.getId());
            return layout != null && layout.onMouseDragged(mouseX - windowX, mouseY - windowY, mouseButton, deltaX, deltaY);
        }
        return false;
    }
}
