package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.task.CloseLaptopTask;
import com.ocelot.opendevices.core.task.MoveWindowTask;
import com.ocelot.opendevices.core.window.Window;
import com.ocelot.opendevices.core.window.WindowClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LaptopScreen extends Screen
{
    private LaptopTileEntity laptop;
    private ClientLaptopDesktop desktop;
    private int posX;
    private int posY;
    private WindowClient draggingWindow;

    public LaptopScreen(LaptopTileEntity laptop)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.laptop = laptop;
        this.desktop = new ClientLaptopDesktop(this.laptop);
        if (this.laptop.getDesktop().getWindowStack().isEmpty())
        {
            this.laptop.getDesktop().openApplicationTest();
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        this.posX = (width - DeviceConstants.LAPTOP_GUI_WIDTH) / 2;
        this.posY = (height - DeviceConstants.LAPTOP_GUI_HEIGHT) / 2;
        this.draggingWindow = null;
    }

    @Override
    public void tick()
    {
        Minecraft minecraft = this.getMinecraft();

        if (!minecraft.player.isAlive() || this.laptop == null || this.laptop.isRemoved())
        {
            minecraft.player.closeScreen();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = this.getMinecraft();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        this.renderBackground();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.textureManager.bindTexture(DeviceConstants.LAPTOP_GUI);

        {
            /* Screen Corners */
            this.blit(this.posX, this.posY, 0, 0, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER); // TOP-LEFT
            this.blit(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_BORDER, this.posY, DeviceConstants.LAPTOP_BORDER + 1, 0, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER); // TOP-RIGHT
            this.blit(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER); // BOTTOM-RIGHT
            this.blit(this.posX, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_BORDER, 0, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER); // BOTTOM-LEFT

            /* Screen Edges */
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_BORDER, this.posY, DeviceConstants.LAPTOP_BORDER, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_BORDER, 1, DeviceConstants.LAPTOP_BORDER); // TOP
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_BORDER, this.posY + DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_BORDER, 1); // RIGHT
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_BORDER, 1, DeviceConstants.LAPTOP_BORDER); // BOTTOM
            RenderUtil.drawRectWithTexture(this.posX, this.posY + DeviceConstants.LAPTOP_BORDER, 0, DeviceConstants.LAPTOP_BORDER + 1, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_BORDER, 1); // LEFT

            /* Screen Center */
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_BORDER, posY + DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_BORDER, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, 1, 1);
        }

        /* Renders the Desktop */
        this.desktop.render(minecraft, fontRenderer, this.posX + DeviceConstants.LAPTOP_BORDER, this.posY + DeviceConstants.LAPTOP_BORDER, partialTicks);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (this.draggingWindow != null)
        {
            this.draggingWindow.move((float) deltaX, (float) deltaY);
            TaskManager.sendTaskToNearbyExceptSender(new MoveWindowTask(this.laptop.getPos(), this.draggingWindow.getId(), (float) deltaX, (float) deltaY));
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        Desktop desktop = this.laptop.getDesktop();
        Window[] windows = desktop.getWindows();
        if (this.draggingWindow == null)
        {
            for (Window window : windows)
            {
                if (window instanceof WindowClient)
                {
                    WindowClient clientWindow = (WindowClient) window;
                    if (clientWindow.isWithinWindowBar(this.posX + DeviceConstants.LAPTOP_BORDER, this.posY + DeviceConstants.LAPTOP_BORDER, mouseX, mouseY, Minecraft.getInstance().getRenderPartialTicks()))
                    {
                        this.draggingWindow = clientWindow;
                        break;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        if (this.draggingWindow != null)
        {
            this.draggingWindow = null;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void removed()
    {
        if (this.laptop != null)
        {
            TaskManager.sendTask(new CloseLaptopTask(this.laptop.getPos()));
        }
    }
}
