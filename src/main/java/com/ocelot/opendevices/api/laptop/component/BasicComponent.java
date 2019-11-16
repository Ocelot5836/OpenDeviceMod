package com.ocelot.opendevices.api.laptop.component;

/**
 * <p>A simple implementation of {@link Component} that defines some common data shared across all components.</p>
 *
 * @author Ocelot
 */
public abstract class BasicComponent implements Component
{
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

    /**
     * @return The x position of the start of this window frame
     */
    public float getWindowX()
    {
        return windowX;
    }

    /**
     * @return The y position of the start of this window frame
     */
    public float getWindowY()
    {
        return windowY;
    }

    @Override
    public void setWindowPosition(float windowX, float windowY)
    {
        this.windowX = windowX;
        this.windowY = windowY;
    }
}
