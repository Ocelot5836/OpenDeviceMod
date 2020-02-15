package com.ocelot.opendevices.api.laptop.application;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>An application is a window that can be opened and used by the user via the {@link Desktop}.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public abstract class Application extends AbstractGui implements WindowContent
{
    private Window window;
    private Layout currentLayout;

    public Application()
    {
        this.currentLayout = new Layout();
    }

    @Override
    public void init(@Nullable CompoundNBT data)
    {
    }

    @Override
    public void update()
    {
        this.currentLayout.update();
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.pushScissor(x, y, this.getWindow().getWidth() - 2, this.getWindow().getHeight() - 2 - DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 0);
        this.currentLayout.setWindowPosition(x, y);
        this.currentLayout.render(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
        RenderUtil.popScissor();
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        this.currentLayout.setWindowPosition(x, y);
        this.currentLayout.renderOverlay(renderer, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return this.currentLayout.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return this.currentLayout.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        return this.currentLayout.onMouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        this.currentLayout.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return this.currentLayout.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean onKeyPressed(int keyCode)
    {
        return this.currentLayout.onKeyPressed(keyCode);
    }

    @Override
    public boolean onKeyReleased(int keyCode)
    {
        return this.currentLayout.onKeyReleased(keyCode);
    }

    @Override
    public void markDirty()
    {
        this.currentLayout.markDirty();
    }

    @Override
    public void removeDirtyMark()
    {
        this.currentLayout.removeDirtyMark();
    }

    @Override
    public void load(CompoundNBT nbt)
    {
    }

    @Override
    public void save(CompoundNBT nbt)
    {
    }

    @Override
    public void saveState(CompoundNBT nbt)
    {
        nbt.put("currentLayout", this.currentLayout.serializeNBT());
    }

    @Override
    public void loadState(CompoundNBT nbt)
    {
        this.currentLayout.deserializeNBT(nbt.getCompound("currentLayout"));
        this.currentLayout.setContent(this.window);
        this.currentLayout.onLayoutLoad();

        if (this.currentLayout.getWidth() != this.window.getContentWidth() || this.currentLayout.getHeight() != this.window.getContentHeight())
        {
            this.window.setSize(this.currentLayout.getWidth(), this.currentLayout.getHeight());
            this.window.center();
        }
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
    public String getTitle()
    {
        return this.getInfo().getName();
    }

    @Override
    public TextureAtlasSprite getIconSprite()
    {
        return ApplicationManager.getAppIcon(this.getRegistryName());
    }

    @Override
    public ResourceLocation getIconMapLocation()
    {
        return ApplicationManager.LOCATION_APP_ICON_TEXTURE;
    }

    @Override
    public final Window getWindow()
    {
        return window;
    }

    @Override
    public boolean isDirty()
    {
        return this.currentLayout.isDirty();
    }

    /**
     * @return The registry name of this application
     */
    public final ResourceLocation getRegistryName()
    {
        return ApplicationManager.getRegistryName(this.getClass());
    }

    /**
     * @return The additional information associated with this application
     */
    public final AppInfo getInfo()
    {
        return ApplicationManager.getAppInfo(this.getRegistryName());
    }

    /**
     * @return The current layout displayed
     */
    public Layout getCurrentLayout()
    {
        return currentLayout;
    }

    /**
     * Sets the current layout to the new one specified. In order to call during a tick, use {@link Laptop#execute(Runnable)}
     *
     * @param layout The new layout to display
     */
    public void setCurrentLayout(Layout layout)
    {
        this.currentLayout.onLayoutUnload();
        this.currentLayout = layout;
        this.currentLayout.setContent(this.window);
        this.currentLayout.onLayoutLoad();

        if (layout.getWidth() != this.window.getContentWidth() || layout.getHeight() != this.window.getContentHeight())
        {
            this.window.setSize(this.currentLayout.getWidth(), this.currentLayout.getHeight());
            this.window.center();// TODO maybe add the ability to center on current position?
        }
    }

    public final void setWindow(Window window)
    {
        if (this.window != null)
            throw new RuntimeException("This method should never be called by the consumer!");
        this.window = window;
    }
}
