package com.ocelot.opendevices.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.CloseLaptopTask;
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

    public LaptopScreen(LaptopTileEntity laptop)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.laptop = laptop;
        this.desktop = new ClientLaptopDesktop(this.laptop);
        this.laptop.getDesktop().openApplicationTest();
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

        minecraft.textureManager.bindTexture(Constants.LAPTOP_GUI);

        /* Physical Screen Position */
        int posX = (this.width - Constants.LAPTOP_GUI_WIDTH) / 2;
        int posY = (this.height - Constants.LAPTOP_GUI_HEIGHT) / 2;

        {
            /* Screen Corners */
            this.blit(posX, posY, 0, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-LEFT
            this.blit(posX + Constants.LAPTOP_GUI_WIDTH - Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER + 1, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-RIGHT
            this.blit(posX + Constants.LAPTOP_GUI_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_GUI_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-RIGHT
            this.blit(posX, posY + Constants.LAPTOP_GUI_HEIGHT - Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-LEFT

            /* Screen Edges */
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // TOP
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_GUI_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // RIGHT
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_GUI_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // BOTTOM
            RenderUtil.drawRectWithTexture(posX, posY + Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // LEFT

            /* Screen Center */
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_SCREEN_HEIGHT, 1, 1);
        }

        /* Renders the Desktop */
        this.desktop.render(minecraft, fontRenderer, posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return true;
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
