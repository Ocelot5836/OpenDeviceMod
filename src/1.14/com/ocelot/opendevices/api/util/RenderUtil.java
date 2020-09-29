package com.ocelot.opendevices.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.client.ShapeRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ReloadRequirements;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_COMPONENTS;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBImageWrite.*;

/**
 * <p>Contains misc utilities helpful for rendering.</p>
 *
 * @author MrCrayfish, Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class RenderUtil
{
    private static final FloatBuffer COLOR_GET_BUFFER = BufferUtils.createFloatBuffer(4);

    private RenderUtil() {}

    /**
     * Fetches the color of the pixel at the specified position of the currently bound frame buffer.
     *
     * @param x The x position to fetch from
     * @param y The y position to fetch from
     * @return The color value at that position in the frame buffer
     */
    public static int getPixel(int x, int y)
    {
        MainWindow window = Minecraft.getInstance().getMainWindow();
        int scale = (int) window.getGuiScaleFactor();
        COLOR_GET_BUFFER.clear();
        glReadPixels(x * scale, (window.getHeight() - y - 1) * scale, 1, 1, GL_RGBA, GL_FLOAT, COLOR_GET_BUFFER);
        return ((int) (COLOR_GET_BUFFER.get(3) * 0xff) << 24) & 0xff | ((int) (COLOR_GET_BUFFER.get(0) * 0xff) << 16) & 0xff | ((int) (COLOR_GET_BUFFER.get(1) * 0xff) << 8) & 0xff | (int) (COLOR_GET_BUFFER.get(2) * 0xff) & 0xff;
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     * @param fit           How the image should be transformed
     */
    public static void drawRectWithTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight, ImageFit fit)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight, fit);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     * @param fit           How the image should be transformed
     */
    public static void drawRectWithTexture(float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight, ImageFit fit)
    {
        switch (fit)
        {
            case FILL:
            {
                float w1 = height / (textureHeight / textureWidth);
                float w2 = width * (textureWidth / w1);
                ShapeRenderer.drawRectWithTexture(x + Math.max((width - w1) / 2, 0), y, z, u - Math.min((w2 - textureWidth) / 2, 0), v, Math.min(width, w1), height, Math.min(textureWidth, w2), textureHeight, sourceWidth, sourceHeight);
                break;
            }
            case STRETCH:
            {
                ShapeRenderer.drawRectWithTexture(x, y, z, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
                break;
            }
            case TILE:
            {
                float scaleWidth = 1.0F / sourceWidth;
                float scaleHeight = 1.0F / sourceHeight;
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                for (int tileY = 0; tileY < Math.ceil(height / textureHeight); tileY++)
                {
                    for (int tileX = 0; tileX < Math.ceil(width / textureWidth); tileX++)
                    {
                        buffer.pos(x + tileX * textureWidth, y + tileY * textureHeight + textureHeight, z).tex(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
                        buffer.pos(x + tileX * textureWidth + textureWidth, y + tileY * textureHeight + textureHeight, z).tex((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
                        buffer.pos(x + tileX * textureWidth + textureWidth, y + tileY * textureHeight, z).tex((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
                        buffer.pos(x + tileX * textureWidth, y + tileY * textureHeight, z).tex(u * scaleWidth, v * scaleHeight).endVertex();
                    }
                }

                tessellator.draw();
                break;
            }
            case CENTER:
            {
                float x1 = (width - textureWidth) / 2;
                float y1 = (height - textureHeight) / 2;
                ShapeRenderer.drawRectWithTexture(x + Math.max(0, x1), y + Math.max(0, y1), z, u - Math.min(0, x1), v - Math.min(0, y1), Math.min(width, textureWidth), Math.min(height, textureHeight), Math.min(width, textureWidth), Math.min(height, textureHeight), sourceWidth, sourceHeight);
                break;
            }
            case SPAN:
            {
                ShapeRenderer.drawRectWithTexture(x, y, z, u, v, width, height, textureWidth / (textureWidth / textureHeight), textureHeight, sourceWidth, sourceHeight);
                break;
            }
        }
    }

    /**
     * Checks to see if the mouse is within the provided bounds.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param x      The x position of the box
     * @param y      The y position of the box
     * @param width  The width of the box
     * @param height The height of the box
     * @return Whether or not the mouse is within the specified box
     */
    public static boolean isMouseInside(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    /**
     * Sets the GL color to the provided int in the format of 0xAARRGGBB.
     *
     * @param color The color to use
     */
    public static void glColor(int color)
    {
        RenderSystem.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, ((color >> 24) & 0xff) / 255f);
    }

    /**
     * Writes the bound texture to file in the running folder.
     *
     * @param location The location to save the file
     * @param format   The format to use
     */
    public static void saveBoundTextureToFile(String location, ImageFormat format)
    {
        File file = new File(Minecraft.getInstance().gameDir, location);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();

        String fileName = location + "." + format.name().toLowerCase(Locale.ROOT);
        int width = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        int height = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
        int components = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_COMPONENTS);
        int componentsCount = getNumComponents(components);

        ByteBuffer image = BufferUtils.createByteBuffer(width * height * componentsCount);
        glGetTexImage(GL_TEXTURE_2D, 0, components, GL_UNSIGNED_BYTE, image);

        switch (format)
        {
            case PNG:
            {
                stbi_write_png(fileName, width, height, componentsCount, image, 0);
            }
            case TGA:
            {
                stbi_write_tga(fileName, width, height, componentsCount, image);
            }
            case BMP:
            {
                stbi_write_bmp(fileName, width, height, componentsCount, image);
            }
            case JPG:
            {
                stbi_write_jpg(fileName, width, height, componentsCount, image, 100);
            }
        }
    }

    /**
     * Converts the GL type of components to channels.
     *
     * @param components The GL type
     * @return The channels in the type
     */
    public static int getNumComponents(int components)
    {
        if (components == GL_RED || components == GL_GREEN || components == GL_BLUE || components == GL_ALPHA)
            return 1;
        if (components == GL_RGB)
            return 3;
        if (components == GL_RGBA)
            return 4;
        return 0;
    }
}
