package com.ocelot.opendevices.api.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11C.*;

/**
 * TODO documentation
 *
 * @author MrCrayfish, Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class RenderUtil
{
    private static final Stack<Scissor> SCISSOR_STACK = new Stack<>();
    private static final FloatBuffer COLOR_GET_BUFFER = BufferUtils.createFloatBuffer(4);

    private static boolean scissor = glGetBoolean(GL_SCISSOR_TEST);

    /**
     * Specifies the width of the entire non-scaled FBO.
     */
    public static int framebufferHeight = 0;
    /**
     * Specifies the scale factor {@link #framebufferHeight} is modified by. Usually GUI scale setting in Minecraft.
     */
    public static double framebufferScale = 0;

    private RenderUtil() {}

    private static void applyScissor()
    {
        if (!SCISSOR_STACK.isEmpty())
        {
            Scissor scissor = SCISSOR_STACK.peek();
            MainWindow window = Minecraft.getInstance().mainWindow;
            double scale = framebufferScale == 0 ? window.getGuiScaleFactor() : framebufferScale;
            int frameHeight = framebufferHeight == 0 ? window.getFramebufferHeight() : framebufferHeight;
            enableScissor();
            glScissor((int) (scissor.x * scale), (int) (frameHeight - scissor.y * scale - scissor.height * scale), (int) Math.max(0, scissor.width * scale), (int) Math.max(0, scissor.height * scale));
        }
        else
        {
            disableScissor();
        }
    }

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void clearScissorStack()
    {
        SCISSOR_STACK.clear();
    }

    /**
     * Makes it so all rendering calls will only be displayed in the specified rectangle.
     *
     * @param x      The x position of the rectangle
     * @param y      The y position of the rectangle
     * @param width  The x size of the rectangle
     * @param height The y size of the rectangle
     */
    public static void pushScissor(double x, double y, double width, double height)
    {
        if (SCISSOR_STACK.size() > 0)
        {
            Scissor scissor = SCISSOR_STACK.peek();
            x = Math.max(scissor.x, x);
            y = Math.max(scissor.y, y);
            width = x + width > scissor.x + scissor.width ? scissor.x + scissor.width - x : width;
            height = y + height > scissor.y + scissor.height ? scissor.y + scissor.height - y : height;
        }

        SCISSOR_STACK.push(new Scissor(x, y, width, height));
        applyScissor();
    }

    /**
     * Clears the current scissor and restores the previous value.
     */
    public static void popScissor()
    {
        if (!SCISSOR_STACK.isEmpty())
            SCISSOR_STACK.pop();
        applyScissor();
    }

    /**
     * @return Whether or not the scissor stack is currently empty
     */
    public static boolean isScissorStackEmpty()
    {
        return SCISSOR_STACK.isEmpty();
    }

    /**
     * Fetches the color of the pixel at the specified position of the currently bound frame buffer.
     *
     * @param x The x position to fetch from
     * @param y The y position to fetch from
     * @return The color value at that position in the frame buffer
     */
    public static int getPixel(int x, int y)
    {
        MainWindow window = Minecraft.getInstance().mainWindow;
        int scale = (int) window.getGuiScaleFactor();
        COLOR_GET_BUFFER.clear();
        glReadPixels(x * scale, (window.getHeight() - y - 1) * scale, 1, 1, GL_RGBA, GL_FLOAT, COLOR_GET_BUFFER);
        return ((int) (COLOR_GET_BUFFER.get(3) * 0xff) << 24) & 0xff | ((int) (COLOR_GET_BUFFER.get(0) * 0xff) << 16) & 0xff | ((int) (COLOR_GET_BUFFER.get(1) * 0xff) << 8) & 0xff | (int) (COLOR_GET_BUFFER.get(2) * 0xff) & 0xff;
    }

    public static void drawRectWithTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    public static void drawRectWithTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    public static void drawRectWithTexture(float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(x, y, z, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    public static void drawRectWithTexture(float x, float y, float z, float width, float height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1, 1);
    }

    public static void drawRectWithTexture(float x, float y, float width, float height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(x, y, 0, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1, 1);
    }

    public static void drawRectWithTexture(float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
    {
        float scaleWidth = 1.0F / sourceWidth;
        float scaleHeight = 1.0F / sourceHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y, z).tex((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x, y, z).tex(u * scaleWidth, v * scaleHeight).endVertex();
        tessellator.draw();
    }

    public static void drawSizedRectWithTexture(float x, float y, float u, float v, float width, float height, float tileWidth, float tileHeight)
    {
        drawSizedRectWithTexture(x, y, 0, u, v, width, height, tileWidth, tileHeight, 256, 256);
    }

    public static void drawSizedRectWithTexture(float x, float y, float u, float v, float width, float height, float tileWidth, float tileHeight, int sourceWidth, int sourceHeight)
    {
        drawSizedRectWithTexture(x, y, 0, u, v, width, height, tileWidth, tileHeight, sourceWidth, sourceHeight);
    }

    public static void drawSizedRectWithTexture(float x, float y, float z, float u, float v, float width, float height, float tileWidth, float tileHeight)
    {
        drawSizedRectWithTexture(x, y, z, u, v, width, height, tileWidth, tileHeight, 256, 256);
    }

    public static void drawSizedRectWithTexture(float x, float y, float z, float u, float v, float width, float height, float tileWidth, float tileHeight, int sourceWidth, int sourceHeight)
    {
        float scaleWidth = 1.0F / sourceWidth;
        float scaleHeight = 1.0F / sourceHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        /* Corners */
        buffer.pos(x, y + tileHeight, z).tex(u * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y, z).tex((u + tileWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x, y, z).tex(u * scaleWidth, v * scaleHeight).endVertex();

        buffer.pos(x + width - tileWidth, y + tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y + tileHeight, z).tex((u + tileWidth * 3) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y, z).tex((u + tileWidth * 3) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y, z).tex((u + tileWidth * 2) * scaleWidth, v * scaleHeight).endVertex();

        buffer.pos(x, y + height, z).tex(u * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + height, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + height - tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x, y + height - tileHeight, z).tex(u * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();

        buffer.pos(x + width - tileWidth, y + height, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + tileWidth * 3) * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height - tileHeight, z).tex((u + tileWidth * 3) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + height - tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();

        /* Edges */
        buffer.pos(x + tileWidth, y + tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y, z).tex((u + tileWidth * 2) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y, z).tex((u + tileWidth) * scaleWidth, v * scaleHeight).endVertex();

        buffer.pos(x + width - tileWidth, y + height - tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height - tileHeight, z).tex((u + tileWidth * 3) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + width, y + tileHeight, z).tex((u + tileWidth * 3) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();

        buffer.pos(x + tileWidth, y + height, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + height, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 3) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + height - tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + height - tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();

        buffer.pos(x, y + height - tileHeight, z).tex(u * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + height - tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x, y + tileHeight, z).tex(u * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();

        /* Center */
        buffer.pos(x + tileWidth, y + height - tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + height - tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight * 2) * scaleHeight).endVertex();
        buffer.pos(x + width - tileWidth, y + tileHeight, z).tex((u + tileWidth * 2) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();
        buffer.pos(x + tileWidth, y + tileHeight, z).tex((u + tileWidth) * scaleWidth, (v + tileHeight) * scaleHeight).endVertex();

        tessellator.draw();
    }

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
        return fontRenderer.getStringWidth(text) > width ? fontRenderer.trimStringToWidth(text, width - fontRenderer.getStringWidth("...")) + "..." : text;
    }

    public static boolean isMouseInside(double mouseX, double mouseY, double x1, double y1, double x2, double y2)
    {
        return mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
    }

    public static String timeToString(long time)
    {
        int hours = (int) ((Math.floor(time / 1000.0) + 7) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public static void glColor(int color)
    {
        GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, ((color >> 24) & 0xff) / 255f);
    }

    public static void enableScissor()
    {
        if (!scissor)
        {
            glEnable(GL_SCISSOR_TEST);
            scissor = true;
        }
    }

    public static void disableScissor()
    {
        if (scissor)
        {
            glDisable(GL_SCISSOR_TEST);
            scissor = false;
        }
    }

    private static class Scissor
    {
        private double x;
        private double y;
        private double width;
        private double height;

        Scissor(double x, double y, double width, double height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
