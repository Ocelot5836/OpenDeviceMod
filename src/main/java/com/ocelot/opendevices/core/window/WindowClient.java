package com.ocelot.opendevices.core.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.core.render.WindowButton;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;

public class WindowClient extends LaptopWindow
{
    private int screenX;
    private int screenY;
    private float lastX;
    private float lastY;
    private WindowButton closeButton;

    public WindowClient(Laptop laptop, float x, float y, int width, int height)
    {
        super(laptop, x, y, width, height);
        this.closeButton = new WindowButton(laptop, button -> this.close());
    }

    public WindowClient(Laptop laptop, int width, int height)
    {
        super(laptop, width, height);
        this.closeButton = new WindowButton(laptop, button -> this.close());
    }

    public WindowClient(Laptop laptop)
    {
        super(laptop);
        this.closeButton = new WindowButton(laptop, button -> this.close());
    }

    @Override
    public void update()
    {
        super.update();
        this.lastX = this.getX();
        this.lastY = this.getY();
    }

    public void render(int mouseX, int mouseY, int color, float partialTicks)
    {
        renderWindow(this.screenX, this.screenY, this, color, color, partialTicks);
        if (this.getId().equals(this.getLaptop().getDesktop().getFocusedWindowId()))
        {
            renderWindow(this.screenX, this.screenY, this, color, this.getLaptop().readSetting(DeviceConstants.FOCUSED_WINDOW_COLOR), partialTicks);
        }

        GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, 1);
        this.closeButton.setPosition(this, partialTicks);
        this.closeButton.render(mouseX, mouseY, partialTicks);
        GlStateManager.color4f(1, 1, 1, 1);
    }

    public boolean pressButtons(double mouseX, double mouseY)
    {
        if (RenderUtil.isMouseInside(mouseX, mouseY, this.screenX + this.getX() + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1, this.screenY + this.getY(), this.screenX + this.getX() + this.getWidth(), this.screenY + this.getY() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT))
        {
            this.closeButton.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public boolean isWithin(double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, this.screenX + this.getX(), this.screenY + this.getY(), this.screenX + this.getX() + this.getWidth(), this.screenY + this.getY() + this.getHeight());
    }

    public boolean isWithinContent(double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, this.screenX + this.getX() + 1, this.screenY + this.getY() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 1, this.screenX + this.getX() + this.getWidth() - 1, this.screenY + this.getY() + this.getHeight() - 1);
    }

    public boolean isWithinWindowBar(double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, this.screenX + this.getX(), this.screenY + this.getY(), this.screenX + this.getX() + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1, this.screenY + this.getY() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
    }

    public float getInterpolatedX(float partialTicks)
    {
        return this.lastX + (this.getX() - this.lastX) * partialTicks;
    }

    public float getInterpolatedY(float partialTicks)
    {
        return this.lastY + (this.getY() - this.lastY) * partialTicks;
    }

    public void setScreenPosition(int screenX, int screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;
        this.closeButton.setScreenPosition(screenX, screenY);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        super.deserializeNBT(nbt);
        this.lastX = this.getX();
        this.lastY = this.getY();
    }

    // TODO improve
    public static void renderWindow(int posX, int posY, WindowClient window, int color, int borderColor, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        RenderUtil.glColor(borderColor);

        float windowX = window.getInterpolatedX(partialTicks);
        float windowY = window.getInterpolatedY(partialTicks);
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        /* Corners */
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY, 0, 0, 1, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 13, posY + windowY, 2, 0, 13, 13, 13, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 1, posY + windowY + windowHeight - 1, 14, 14, 1, 1, 1, 1);
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY + windowHeight - 1, 0, 14, 1, 1, 1, 1);

        /* Edges */
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY, 1, 0, windowWidth - 14, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 1, posY + windowY + 13, 14, 13, 1, windowHeight - 14, 1, 1);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + windowHeight - 1, 1, 14, windowWidth - 2, 1, 13, 1);
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY + 13, 0, 13, 1, windowHeight - 14, 1, 1);

        /* Center */
        RenderUtil.glColor(color);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + 13, 1, 13, windowWidth - 2, windowHeight - 14, 13, 1);

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
