package com.ocelot.opendevices.api.laptop.application;

import com.ocelot.opendevices.api.DeviceComponents;
import com.ocelot.opendevices.api.DeviceConstants;
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

import javax.annotation.Nullable;

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
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        if (this.currentLayout != null)
        {
            RenderUtil.enableScissor();
            RenderUtil.pushScissor(x, y, this.getWindow().getWidth() - 2, this.getWindow().getHeight() - 2 - DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
            this.currentLayout.setWindowPosition(x, y);
            this.currentLayout.render(mouseX, mouseY, partialTicks);
            RenderUtil.popScissor();
            RenderUtil.disableScissor();
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
        nbt.put("currentLayout", DeviceComponents.serializeComponent(this.currentLayout));
    }

    @Override
    public void loadState(CompoundNBT nbt)
    {
        Layout layout = DeviceComponents.deserializeComponent(nbt.getCompound("currentLayout"));

        this.currentLayout.onLayoutUnload();
        this.currentLayout = layout;
        this.currentLayout.onLayoutLoad();

        if (layout.getWidth() != this.window.getContentWidth() || layout.getHeight() != this.window.getContentHeight())
        {
            this.window.setSize(this.currentLayout.getWidth(), this.currentLayout.getHeight());
            this.window.center();
            // TODO maybe add the ability to center on current position?
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

    /**
     * <p>Marks this application as having changes and saves it to file and other clients.</p>
     * <p>Should be called after each modification to a value that needs to be synced.</p>
     */
    protected void markDirty()
    {
        this.window.markDirty();
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
     * @return The current layout displayed
     */
    public Layout getCurrentLayout()
    {
        return currentLayout;
    }

    /**
     * Sets the current layout to the new one specified on the next update.
     *
     * @param layout The new layout to display
     */
    public void setCurrentLayout(Layout layout)
    {
        this.window.getLaptop().execute(() ->
        {
            this.currentLayout.onLayoutUnload();
            this.currentLayout = layout;
            this.currentLayout.onLayoutLoad();

            if (layout.getWidth() != this.window.getWidth() || layout.getHeight() != this.window.getHeight())
            {
                this.window.setSize(this.currentLayout.getWidth(), this.currentLayout.getHeight());
                this.window.center();// TODO maybe add the ability to center on current position?
            }

            this.markDirty();
        });
    }

    public final void setWindow(Window window)
    {
        if (this.window != null)
            throw new RuntimeException("This method should never be called by the consumer!");
        this.window = window;
    }
}
