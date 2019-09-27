package com.ocelot.opendevices.core.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class WindowClient extends Window
{
    private float lastX;
    private float lastY;

    public WindowClient(float x, float y, int width, int height)
    {
        super(x, y, width, height);
    }

    public WindowClient(int width, int height)
    {
        super(width, height);
    }

    @Override
    public void update()
    {
        super.update();
        this.lastX = this.getX();
        this.lastY = this.getY();
    }

    public void render(int posX, int posY, int color, float partialTicks)
    {
        renderWindow(posX, posY, this, color, partialTicks);
    }

    public float getLastX()
    {
        return lastX;
    }

    public float getLastY()
    {
        return lastY;
    }

    public float getInterpolatedX(float partialTicks)
    {
        return this.getLastX() + (this.getX() - this.lastX) * partialTicks;
    }

    public float getInterpolatedY(float partialTicks)
    {
        return this.getLastY() + (this.getY() - this.lastY) * partialTicks;
    }

    public boolean isWithin(float posX, float posY, double mouseX, double mouseY, float partialTicks)
    {
        float x = this.getInterpolatedX(partialTicks);
        float y = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + x, posY + y, posX + x + this.getWidth(), posY + y + this.getHeight());
    }

    public boolean isWithinContent(float posX, float posY, double mouseX, double mouseY, float partialTicks)
    {
        float x = this.getInterpolatedX(partialTicks);
        float y = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + x + 1, posY + y + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 1, posX + x + this.getWidth() - 1, posY + y + this.getHeight() - 1);
    }

    public boolean isWithinWindowBar(float posX, float posY, double mouseX, double mouseY, float partialTicks)
    {
        float x = this.getInterpolatedX(partialTicks);
        float y = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + x, posY + y, posX + x + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, posY + y + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
    }

    // TODO improve
    public static void renderWindow(int posX, int posY, WindowClient window, int color, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, ((color >> 24) & 0xff) / 255f);

        /* Corners */
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks), posY + window.getInterpolatedY(partialTicks), 0, 0, 1, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + window.getWidth() - 13, posY + window.getInterpolatedY(partialTicks), 2, 0, 13, 13, 13, 13);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + window.getWidth() - 1, posY + window.getInterpolatedY(partialTicks) + window.getHeight() - 1, 14, 14, 1, 1, 1, 1);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks), posY + window.getInterpolatedY(partialTicks) + window.getHeight() - 1, 0, 14, 1, 1, 1, 1);

        /* Edges */
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + 1, posY + window.getInterpolatedY(partialTicks), 1, 0, window.getWidth() - 14, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + window.getWidth() - 1, posY + window.getInterpolatedY(partialTicks) + 13, 14, 13, 1, window.getHeight() - 14, 1, 1);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + 1, posY + window.getInterpolatedY(partialTicks) + window.getHeight() - 1, 1, 14, window.getWidth() - 2, 1, 13, 1);
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks), posY + window.getInterpolatedY(partialTicks) + 13, 0, 13, 1, window.getHeight() - 14, 1, 1);

        /* Center */
        RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + 1, posY + window.getInterpolatedY(partialTicks) + 13, 1, 13, window.getWidth() - 2, window.getHeight() - 14, 13, 1);

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
