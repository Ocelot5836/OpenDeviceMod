package com.ocelot.opendevices.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.core.laptop.Laptop;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageCloseLaptop;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import com.sun.prism.TextureMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LaptopScreen extends Screen
{
    private static Laptop laptop;

    public LaptopScreen(LaptopTileEntity te)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        laptop = new Laptop(te);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        assert this.minecraft != null;

        this.renderBackground();

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(Constants.LAPTOP_GUI);

        /* Physical Screen */
        int posX = (width - Constants.LAPTOP_DEVICE_WIDTH) / 2;
        int posY = (height - Constants.LAPTOP_DEVICE_HEIGHT) / 2;

        /* Corners */
        this.blit(posX, posY, 0, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-LEFT
        this.blit(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER + 1, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-RIGHT
        this.blit(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-RIGHT
        this.blit(posX, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-LEFT

        /* Edges */
        RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // TOP
        RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // RIGHT
        RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // BOTTOM
        RenderUtil.drawRectWithTexture(posX, posY + Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // LEFT

        /* Center */
        RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_SCREEN_HEIGHT, 1, 1);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void onClose()
    {
        super.onClose();

        laptop.close();
        laptop = null;
    }

    /**
     * @return The laptop instance if this GUI is open, otherwise null
     */
    @Nullable
    public static Laptop getLaptop()
    {
        return laptop;
    }
}
