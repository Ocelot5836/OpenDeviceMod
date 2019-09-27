package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.core.window.WindowClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class WindowButton extends Button
{
    private Laptop laptop;
    private int screenX;
    private int screenY;
    private float posX;
    private float posY;

    public WindowButton(Laptop laptop, IPressable onPress)
    {
        super(0, 0, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, "", onPress);
        this.laptop = laptop;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            Minecraft minecraft = Minecraft.getInstance();
            Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
            int color = this.laptop.readSetting(DeviceConstants.WINDOW_BUTTON_COLOR);
            GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, 1);

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            //			this.drawTexturedModalRect(this.x, this.y, state * this.width + 15, 0, this.width, this.height);
            GlStateManager.pushMatrix();
            {
                GlStateManager.translatef(this.posX, this.posY, 0);
                this.blit(this.screenX, this.screenY, 26 + this.getYImage(RenderUtil.isMouseInside(mouseX, mouseY, this.screenX + this.posX, this.screenY + this.posY, this.screenX + this.posX + this.width, this.screenY + this.posY + this.height)) * DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, 0, this.width, this.height);
            }
            GlStateManager.popMatrix();
        }
    }

    public void setScreenPosition(int screenX, int screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public void setPosition(WindowClient window, float partialTicks)
    {
        this.posX = window.getInterpolatedX(partialTicks) + window.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1;
        this.posY = window.getInterpolatedY(partialTicks) + 1;
    }
}
