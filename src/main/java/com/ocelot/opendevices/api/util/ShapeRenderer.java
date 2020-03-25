package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.GL_QUADS;

/**
 * <p>Renders {@link GL11C#GL_QUADS} to the screen using enhanced precision and {@link BufferBuilder}.</p>
 * <p>To use chain rendering, use {@link #begin()} to start rendering and {@link #end()} to complete a batch.</p>
 *
 * @author Ocelot
 */
@OnlyIn(Dist.CLIENT)
public class ShapeRenderer
{
    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(x, y, 0, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1.0, 1.0);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(double x, double y, double u, double v, double width, double height)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, width, height, 256, 256);
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
     */
    public static void drawRectWithTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, 256, 256);
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
     */
    public static void drawRectWithTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight, double sourceWidth, double sourceHeight)
    {
        drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1.0, 1.0);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(double x, double y, double z, double u, double v, double width, double height)
    {
        drawRectWithTexture(x, y, z, u, v, width, height, width, height, 256, 256);
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
     */
    public static void drawRectWithTexture(double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight)
    {
        drawRectWithTexture(x, y, z, u, v, width, height, textureWidth, textureHeight, 256, 256);
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
     */
    public static void drawRectWithTexture(double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight, double sourceWidth, double sourceHeight)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        begin();
        drawRectWithTexture(buffer, x, y, z, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
        tessellator.draw();
    }

    /**
     * Begins the rendering of a chain of quads.
     */
    public static BufferBuilder begin()
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        return buffer;
    }

    /**
     * Ends the rendering of a chain of quads.
     */
    public static void end()
    {
        Tessellator.getInstance().draw();
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, x, y, 0, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1.0, 1.0);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double u, double v, double width, double height)
    {
        drawRectWithTexture(buffer, x, y, 0, u, v, width, height, width, height, 256, 256);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight)
    {
        drawRectWithTexture(buffer, x, y, 0, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
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
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight, double sourceWidth, double sourceHeight)
    {
        drawRectWithTexture(buffer, x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1.0, 1.0);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double z, double u, double v, double width, double height)
    {
        drawRectWithTexture(buffer, x, y, z, u, v, width, height, width, height, 256, 256);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight)
    {
        drawRectWithTexture(buffer, x, y, z, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
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
     */
    public static void drawRectWithTexture(BufferBuilder buffer, double x, double y, double z, double u, double v, double width, double height, double textureWidth, double textureHeight, double sourceWidth, double sourceHeight)
    {
        if (buffer.getDrawMode() != GL_QUADS)
        {
            OpenDevices.LOGGER.error("Attempted to draw rect with a texture, but the draw mode is not set to GL_QUADS.");
            return;
        }
        double scaleWidth = 1.0 / sourceWidth;
        double scaleHeight = 1.0 / sourceHeight;
        buffer.pos(x, y + height, z).tex(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(x + width, y, z).tex((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(x, y, z).tex(u * scaleWidth, v * scaleHeight).endVertex();
    }
}
