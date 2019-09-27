package com.ocelot.opendevices.core.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class WindowClient extends Window
{
    private float lastX;
    private float lastY;

    public WindowClient(Laptop laptop, float x, float y, int width, int height)
    {
        super(laptop, x, y, width, height);
    }

    public WindowClient(Laptop laptop, int width, int height)
    {
        super(laptop, width, height);
    }

    public WindowClient(Laptop laptop)
    {
        super(laptop);
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
        renderWindow(posX, posY, this, color, partialTicks, false);
        if (this.getId().equals(this.getLaptop().getDesktop().getFocusedWindowId()))
        {
            renderWindow(posX, posY, this, 0xff00ff, partialTicks, true);
        }
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

    public boolean isWithin(float posX, float posY, double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + this.getX(), posY + this.getY(), posX + this.getX() + this.getWidth(), posY + this.getY() + this.getHeight());
    }

    public boolean isWithinContent(float posX, float posY, double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + this.getX() + 1, posY + this.getY() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 1, posX + this.getX() + this.getWidth() - 1, posY + this.getY() + this.getHeight() - 1);
    }

    public boolean isWithinWindowBar(float posX, float posY, double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, posX + this.getX(), posY + this.getY(), posX + this.getX() + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, posY + this.getY() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
    }

    // TODO improve
    public static void renderWindow(int posX, int posY, WindowClient window, int color, float partialTicks, boolean cutout)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, 1);

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
        if (!cutout)
        {
            RenderUtil.drawRectWithTexture(posX + window.getInterpolatedX(partialTicks) + 1, posY + window.getInterpolatedY(partialTicks) + 13, 1, 13, window.getWidth() - 2, window.getHeight() - 14, 13, 1);
        }

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
