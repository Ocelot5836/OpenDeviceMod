package com.ocelot.opendevices.core.laptop.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.application.Application;
import com.ocelot.opendevices.api.laptop.application.ApplicationManager;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.render.WindowButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class WindowClient extends LaptopWindow
{
    private int screenX;
    private int screenY;
    private float lastX;
    private float lastY;
    private WindowContent content;
    private WindowButton closeButton;

    public WindowClient(LaptopTileEntity laptop)
    {
        super(laptop);
        this.closeButton = new WindowButton(laptop, button -> this.close());
    }

    public WindowClient(LaptopTileEntity laptop, @Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        super(laptop, initData, contentType, contentId, width, height);
        this.content = createContent(contentType, contentId);
        this.closeButton = new WindowButton(laptop, button -> this.close());
    }

    private WindowContent createContent(WindowContentType contentType, ResourceLocation contentId)
    {
        switch (contentType)
        {
            case APPLICATION:
                Application app = ApplicationManager.createApplication(contentId);
                if (app != null)
                {
                    app.setWindow(this);
                    return app;
                }
            case DIALOG:
            default:
                return null;
        }
    }

    @Override
    public void create()
    {
        this.content.create();
    }

    @Override
    public void init()
    {
        this.content.init(this.getInitData());
    }

    @Override
    public void update()
    {
        super.update();
        this.lastX = this.getX();
        this.lastY = this.getY();
        this.content.update();
    }

    public void render(int mouseX, int mouseY, int color, float partialTicks)
    {
        renderWindow(this.screenX, this.screenY, this, color, this.getId().equals(this.getLaptop().getDesktop().getFocusedWindowId()) ? this.getLaptop().readSetting(LaptopSettings.FOCUSED_WINDOW_COLOR) : color, partialTicks);

        this.content.render(this.screenX + this.getInterpolatedX(partialTicks) + 1, this.screenY + this.getInterpolatedY(partialTicks) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT, mouseX, mouseY, partialTicks);

        TextureAtlasSprite icon = this.content.getIconSprite();
        int titleOffset = 0;
        if (icon != null)
        {
            Minecraft.getInstance().getTextureManager().bindTexture(this.content.getIconMapLocation());
            RenderUtil.drawRectWithTexture(this.screenX + this.getInterpolatedX(partialTicks) + 2.5f, this.screenY + this.getInterpolatedY(partialTicks) + 2.5f, 8, 8, icon);
            titleOffset = 10;
        }

        String title = this.content.getTitle();
        if (title != null)
        {
            RenderUtil.drawStringClipped(Minecraft.getInstance().fontRenderer, title, this.screenX + this.getInterpolatedX(partialTicks) + 2f + titleOffset, this.screenY + this.getInterpolatedY(partialTicks) + 2.5f, this.getWidth() - 4 - titleOffset - this.closeButton.getWidth(), this.getLaptop().readSetting(LaptopSettings.DESKTOP_TEXT_COLOR), true);
        }

        RenderUtil.glColor(0xff000000 | color);
        this.closeButton.setPosition(this, partialTicks);
        this.closeButton.render(mouseX, mouseY, partialTicks);
        RenderUtil.glColor(0xffffffff);
    }

    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        this.content.renderOverlay(renderer, this.screenX + this.getInterpolatedX(partialTicks) + 1, this.screenY + this.getInterpolatedY(partialTicks) + 1 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGainFocus()
    {
        this.content.onGainFocus();
    }

    @Override
    public void onLostFocus()
    {
        this.content.onLostFocus();
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return this.content.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return this.content.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double detaY)
    {
        return this.content.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, detaY);
    }

    @Override
    public boolean onKeyPressed(int keyCode)
    {
        return this.content.onKeyPressed(keyCode);
    }

    @Override
    public boolean onKeyReleased(int keyCode)
    {
        return this.content.onKeyReleased(keyCode);
    }

    @Override
    public void onClose()
    {
        this.content.onClose();
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

    /**
     * @return The physical content that is being displayed by this window
     */
    public WindowContent getContent()
    {
        return content;
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
        return (int) this.lastX + ((int) this.getX() - (int) this.lastX) * partialTicks;
    }

    public float getInterpolatedY(float partialTicks)
    {
        return (int) this.lastY + ((int) this.getY() - (int) this.lastY) * partialTicks;
    }

    public void setScreenPosition(int screenX, int screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;
        this.closeButton.setScreenPosition(screenX, screenY);
    }

    @Override
    public CompoundNBT getContentData()
    {
        CompoundNBT contentData = new CompoundNBT();
        this.content.saveState(contentData);
        return contentData;
    }

    @Override
    public void setContentData(CompoundNBT contentData)
    {
        this.content.loadState(contentData);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = super.serializeNBT();

        CompoundNBT contentDataNBT = new CompoundNBT();
        this.content.save(contentDataNBT);
        nbt.put("contentData", contentDataNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        super.deserializeNBT(nbt);

        // TODO maybe save data and fully close if server decides to change app
        if (this.content == null || (this.getContentType() == WindowContentType.APPLICATION && !this.getContentId().equals(ApplicationManager.getRegistryName(this.content.getClass()))))
            this.content = createContent(this.getContentType(), this.getContentId());

        this.content.load(nbt.getCompound("contentData"));
        this.lastX = this.getX();
        this.lastY = this.getY();
    }

    // TODO improve
    private static void renderWindow(int posX, int posY, WindowClient window, int color, int borderColor, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        RenderUtil.glColor(0xff000000 | borderColor);

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
        RenderUtil.glColor(0xff000000 | color);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + 13, 1, 13, windowWidth - 2, windowHeight - 14, 13, 1);

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
