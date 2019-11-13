package com.ocelot.opendevices.api.laptop.window.application;

import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

/**
 * <p>An application is a window that can be opened and used by the user via the {@link Desktop}.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public abstract class Application extends AbstractGui implements WindowContent
{
    private Window window;

    @Override
    public void update()
    {

    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("Actual Application Content", x, y, 0xffffffff);
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
        return ClientApplicationManager.getAppIcon(this.getRegistryName());
    }

    @Override
    public ResourceLocation getIconMapLocation()
    {
        return ClientApplicationManager.LOCATION_APP_ICON_TEXTURE;
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
    public final AppInfo getInfo(){
        return ClientApplicationManager.getAppInfo(this.getRegistryName());
    }

    public final void setWindow(Window window)
    {
        if(this.window != null)
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
