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

    private RenderUtil() {}

    private static void restoreScissor()
    {
        if (!SCISSOR_STACK.isEmpty())
        {
            Scissor scissor = SCISSOR_STACK.peek();
            MainWindow window = Minecraft.getInstance().mainWindow;
            int scale = (int) window.getGuiScaleFactor();
            enableScissor();
            glScissor(scissor.x * scale, window.getFramebufferHeight() - scissor.y * scale - scissor.height * scale, Math.max(0, scissor.width * scale), Math.max(0, scissor.height * scale));
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
    public static void pushScissor(float x, float y, float width, float height)
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
        restoreScissor();
    }

    /**
     * Clears the current scissor and restores the previous value.
     */
    public static void popScissor()
    {
        if (!SCISSOR_STACK.isEmpty())
        {
            SCISSOR_STACK.pop();
        }
        if (SCISSOR_STACK.isEmpty())
            disableScissor();
        restoreScissor();
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
            clipped = fontRenderer.trimStringToWidth(clipped, width - fontRenderer.getStringWidth("...")) + "...";
        }
        return clipped;
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
        private int x;
        private int y;
        private int width;
        private int height;

        Scissor(float x, float y, float width, float height)
        {
            this.x = Math.round(x);
            this.y = Math.round(y);
            this.width = Math.round(width);
            this.height = Math.round(height);
        }
    }
}
