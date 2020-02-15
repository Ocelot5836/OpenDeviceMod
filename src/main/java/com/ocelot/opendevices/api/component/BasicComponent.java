package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.laptop.window.DesktopContent;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.gui.AbstractGui;

/**
 * <p>A simple implementation of {@link Component} that defines some common data shared across all components.</p>
 *
 * @author Ocelot
 * @see Layout
 */
public abstract class BasicComponent extends AbstractGui implements Component
{
    private DesktopContent content;
    private float windowX;
    private float windowY;
    private boolean dirty;

    @Override
    public void markDirty()
    {
        this.dirty = true;
    }

    @Override
    public void removeDirtyMark()
    {
        this.dirty = false;
    }

    @Override
    public void update()
    {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
    }

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
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
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
    public DesktopContent getContent()
    {
        return content;
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
    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public void setContent(DesktopContent content)
    {
        this.content = content;
    }

    @Override
    public void setWindowPosition(float windowX, float windowY)
    {
        this.windowX = windowX;
        this.windowY = windowY;
    }
}
