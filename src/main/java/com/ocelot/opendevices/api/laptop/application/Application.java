package com.ocelot.opendevices.api.laptop.application;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <p>An application is a window that can be opened and used by the user via the {@link Desktop}.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
@OnlyIn(Dist.CLIENT)
public abstract class Application extends AbstractGui implements WindowContent
{
    private Window window;
    private Layout currentLayout;
    private Layout nextLayout;
    private LayoutProtocol nextProtocol;

    public Application()
    {
        this.currentLayout = new Layout();
    }

    @Override
    public void update()
    {
        if (this.nextLayout != null)
        {
            this.currentLayout.onLayoutUnload();
            this.currentLayout = this.nextLayout;
            this.nextLayout.onLayoutLoad();

            this.window.setSize((int) this.currentLayout.getWidth(), (int) this.currentLayout.getHeight());

            switch (this.nextProtocol)
            {
                case NOTHING:
                    break;
                case RESET:
                    this.window.center();
                    break;
            }

            this.nextLayout = null;
            this.nextProtocol = null;
        }
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        if (this.currentLayout != null)
        {
            RenderUtil.pushScissor(x, y, this.getWindow().getWidth(), this.getWindow().getHeight());
            this.currentLayout.setWindowPosition(x, y);
            this.currentLayout.render(mouseX, mouseY, partialTicks);
            RenderUtil.popScissor();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        if (this.currentLayout != null)
        {
            this.currentLayout.setWindowPosition(x, y);
            this.currentLayout.renderOverlay(renderer, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return this.currentLayout != null && this.currentLayout.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return this.currentLayout != null && this.currentLayout.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return this.currentLayout != null && this.currentLayout.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean onKeyPressed(int keyCode)
    {
        return this.currentLayout != null && this.currentLayout.onKeyPressed(keyCode);
    }

    @Override
    public boolean onKeyReleased(int keyCode)
    {
        return this.currentLayout != null && this.currentLayout.onKeyReleased(keyCode);
    }

    @Override
    public void saveState(CompoundNBT nbt)
    {
    }

    @Override
    public void loadState(CompoundNBT nbt)
    {
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
     * Sets the current layout to the new one specified on the next update.
     *
     * @param layout   The new layout to display
     * @param protocol The way to use the layout to modify the window
     */
    public void setCurrentLayout(Layout layout, LayoutProtocol protocol)
    {
        this.nextLayout = layout;
        this.nextProtocol = protocol;
    }

    public final void setWindow(Window window)
    {
        if (this.window != null)
            throw new RuntimeException("This method should never be called by the consumer!");
        this.window = window;
    }

    /**
     * Registers a new type of window content that can be opened by the {@link Desktop} as a window.
     *
     * @author Ocelot
     * @see WindowContent
     */
    public @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>. <b><i>Will not register unless mod id is provided!</i></b>
         */
        String value();
    }
}
