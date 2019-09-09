package com.ocelot.opendevices.api.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

//TODO documentation
public class RenderUtil
{
    public static void renderItem(int x, int y, ItemStack stack, boolean overlay)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        GlStateManager.disableDepthTest();
        GlStateManager.enableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        if (overlay) itemRenderer.renderItemOverlays(mc.fontRenderer, stack, x, y);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableLighting();
    }

    public static void drawRectWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    public static void drawRectWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    public static void drawRectWithTexture(double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(x, y, z, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    public static void drawRectWithTexture(double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
    {
        float scaleWidth = 1.0F / sourceWidth;
        float scaleHeight = 1.0F / sourceHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(u * scaleWidth, (double) (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height, z).tex((double) (u + textureWidth) * scaleWidth, (double) (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y, z).tex((double) (u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x, y, z).tex(u * scaleWidth, v * scaleHeight).endVertex();
        tessellator.draw();
    }

    //    public static void drawApplicationIcon(@Nullable AppInfo info, double x, double y)
    //    {
    //        //TODO: Reset color GlStateManager.color(1.0F, 1.0F, 1.0F);
    //        Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
    //        if (info != null)
    //        {
    //            drawRectWithTexture(x, y, info.getIconU(), info.getIconV(), 14, 14, 14, 14, 224, 224);
    //        }
    //        else
    //        {
    //            drawRectWithTexture(x, y, 0, 0, 14, 14, 14, 14, 224, 224);
    //        }
    //    }

    public static void drawStringClipped(FontRenderer fontRenderer, String text, float x, float y, int width, int color, boolean shadow)
    {
        if (shadow)
        {
            fontRenderer.drawStringWithShadow(clipStringToWidth(fontRenderer, text, width), x, y, color);
        }
        else
        {
            fontRenderer.drawString(clipStringToWidth(fontRenderer, text, width), x, y, color);
        }
    }

    public static String clipStringToWidth(FontRenderer fontRenderer, String text, int width)
    {
        String clipped = text;
        if (fontRenderer.getStringWidth(clipped) > width)
        {
            clipped = fontRenderer.trimStringToWidth(clipped, width - 8) + "...";
        }
        return clipped;
    }

    public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
    {
        return mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
    }
}