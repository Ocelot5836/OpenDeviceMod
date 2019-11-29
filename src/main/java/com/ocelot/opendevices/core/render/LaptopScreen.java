package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import com.ocelot.opendevices.core.LaptopDesktop;
import com.ocelot.opendevices.core.LaptopTaskBar;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.laptop.application.TestApplication;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import com.ocelot.opendevices.core.laptop.window.WindowClient;
import com.ocelot.opendevices.core.task.CloseLaptopTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class LaptopScreen extends Screen implements TooltipRenderer
{
    private LaptopTileEntity laptop;
    private int posX;
    private int posY;
    private boolean clickable;
    private WindowClient draggingWindow;

    public LaptopScreen(LaptopTileEntity laptop)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.laptop = laptop;
        this.laptop.getDesktop().openApplication(TestApplication.REGISTRY_NAME);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        this.posX = (width - DeviceConstants.LAPTOP_GUI_WIDTH) / 2;
        this.posY = (height - DeviceConstants.LAPTOP_GUI_HEIGHT) / 2;
        this.draggingWindow = null;
        this.clickable = false;
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
        /* Partial Ticks are still broken :/ */
        partialTicks = Minecraft.getInstance().getRenderPartialTicks();

        Minecraft minecraft = this.getMinecraft();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        this.renderBackground();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.textureManager.bindTexture(DeviceConstants.LAPTOP_GUI);

        {
            /* Screen Corners */
            this.blit(this.posX, this.posY, 0, 0, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER); // TOP-LEFT
            this.blit(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY, DeviceConstants.LAPTOP_GUI_BORDER + 1, 0, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER); // TOP-RIGHT
            this.blit(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER); // BOTTOM-RIGHT
            this.blit(this.posX, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER); // BOTTOM-LEFT

            /* Screen Edges */
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY, DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_GUI_BORDER, 1, DeviceConstants.LAPTOP_GUI_BORDER); // TOP
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_GUI_BORDER, 1); // RIGHT
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_GUI_BORDER, 1, DeviceConstants.LAPTOP_GUI_BORDER); // BOTTOM
            RenderUtil.drawRectWithTexture(this.posX, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_GUI_BORDER, 1); // LEFT

            /* Screen Center */
            RenderUtil.drawRectWithTexture(this.posX + DeviceConstants.LAPTOP_GUI_BORDER, posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, 1, 1);
        }

        RenderUtil.framebufferHeight = 0;
        RenderUtil.framebufferScale = 0;

        /* Renders the Content */
        LaptopRenderer.render(this.laptop, minecraft, fontRenderer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, mouseX, mouseY, partialTicks);

        super.render(mouseX, mouseY, partialTicks);

        /* Render the Tooltips */
        LaptopRenderer.renderOverlay(this, this.laptop, minecraft, fontRenderer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderTooltip(ItemStack stack, int posX, int posY)
    {
        super.renderTooltip(stack, posX, posY);
    }

    @Override
    public void renderTooltip(String tooltip, int posX, int posY, FontRenderer fontRenderer)
    {
        this.renderTooltip(Collections.singletonList(tooltip), posX, posY, fontRenderer);
    }

    @Override
    public void renderComponentHoverEffect(ITextComponent textComponent, int posX, int posY)
    {
        super.renderComponentHoverEffect(textComponent, posX, posY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
    {
        if (this.draggingWindow == null)
        {
            LaptopDesktop desktop = this.laptop.getDesktop();
            LaptopWindow focusedWindow = desktop.getFocusedWindow();
            if (focusedWindow != null && focusedWindow.onKeyPressed(keyCode))
            {
                return true;
            }
        }
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (keyCode == 256 || this.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))
        {
            this.getMinecraft().player.closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int p_223281_3_)
    {
        if (this.draggingWindow == null)
        {
            LaptopDesktop desktop = this.laptop.getDesktop();
            LaptopWindow focusedWindow = desktop.getFocusedWindow();
            if (focusedWindow != null && focusedWindow.onKeyReleased(keyCode))
            {
                return true;
            }
        }
        return super.keyReleased(keyCode, scanCode, p_223281_3_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        LaptopDesktop desktop = this.laptop.getDesktop();
        if (this.draggingWindow != null)
        {
            this.draggingWindow.move((float) deltaX, (float) deltaY);
            return true;
        }
        else if (desktop.getFocusedWindow() != null)
        {
            WindowClient window = (WindowClient) desktop.getFocusedWindow();
            if (window.isWithinContent(mouseX, mouseY) && window.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY))
            {
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        Desktop desktop = this.laptop.getDesktop();
        LaptopTaskBar taskBar = this.laptop.getTaskBar();
        Window[] windows = desktop.getWindows();
        boolean loseFocus = true;

        if (!RenderUtil.isMouseInside(mouseX, mouseY, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight(), this.posX + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_WIDTH, this.posY + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_HEIGHT))
        {
            if (this.draggingWindow == null)
            {
                for (int i = 0; i < windows.length; i++)
                {
                    if (!loseFocus)
                        break;

                    WindowClient window = (WindowClient) windows[windows.length - i - 1];
                    if (window.pressButtons(mouseX, mouseY))
                    {
                        return true;
                    }
                    if (window.isWithin(mouseX, mouseY))
                    {
                        window.focus();
                        loseFocus = false;
                    }
                    if (window.isWithinContent(mouseX, mouseY))
                    {
                        if (window.onMousePressed(mouseX, mouseY, mouseButton))
                        {
                            return true;
                        }
                        else
                        {
                            return super.mouseClicked(mouseX, mouseY, mouseButton);
                        }
                    }
                    else if (window.isWithinWindowBar(mouseX, mouseY))
                    {
                        this.draggingWindow = window;
                        return true;
                    }
                    this.clickable = true;
                }
            }

            if (loseFocus)
            {
                desktop.focusWindow(null);
            }
        }
        else
        {
            int size = taskBar.isEnlarged() ? 16 : 8;
            int i = 0;

            Window hoveredWindow = null;
            for (Window value : taskBar.getDisplayedWindows())
            {
                WindowClient window = (WindowClient) value;
                if (RenderUtil.isMouseInside(mouseX, mouseY, this.posX + DeviceConstants.LAPTOP_GUI_BORDER + 4 + (size + 4) * i, this.posY + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight() + 4, this.posX + DeviceConstants.LAPTOP_GUI_BORDER + 4 + (size + 4) * i + size, this.posY + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight() + 4 + size))
                {
                    hoveredWindow = window;
                    break;
                }
                i++;
            }

            if (hoveredWindow != null)
            {
                hoveredWindow.focus();
            }
            else
            {
                desktop.focusWindow(null);
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        Desktop desktop = this.laptop.getDesktop();
        if (this.draggingWindow != null)
        {
            this.draggingWindow = null;
            return true;
        }
        else if (this.clickable && desktop.getFocusedWindow() != null)
        {
            this.clickable = false;
            WindowClient window = (WindowClient) desktop.getFocusedWindow();
            if (window.isWithinContent(mouseX, mouseY) && window.onMouseReleased(mouseX, mouseY, mouseButton))
            {
                return true;
            }
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
            TaskManager.sendToServer(new CloseLaptopTask(this.laptop.getPos()), TaskManager.TaskReceiver.SENDER);
        }
    }
}
