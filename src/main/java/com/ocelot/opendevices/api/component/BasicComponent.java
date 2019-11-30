package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.laptop.window.Window;

/**
 * <p>A simple implementation of {@link Component} that defines some common data shared across all components.</p>
 *
 * @author Ocelot
 * @see Layout
 */
public abstract class BasicComponent implements Component
{
    private Window window;
    private float windowX;
    private float windowY;

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode)
    {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode)
    {
        return false;
    }

    @Override
    public void onGainFocus()
    {
    }

    @Override
    public void onLostFocus()
    {
    }

    @Override
    public void onClose()
    {
    }

    @Override
    public void onLayoutLoad()
    {
    }

    @Override
    public void onLayoutUnload()
    {
    }

    @Override
    public Window getWindow()
    {
        return window;
    }

    @Override
    public float getWindowX()
    {
        return windowX;
    }

    @Override
    public float getWindowY()
    {
        return windowY;
    }

    @Override
    public void setWindow(Window window)
    {
        this.window = window;
    }

    @Override
    public void setWindowPosition(float windowX, float windowY)
    {
        this.windowX = windowX;
        this.windowY = windowY;
    }
}
