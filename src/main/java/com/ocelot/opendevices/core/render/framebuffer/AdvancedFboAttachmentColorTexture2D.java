package com.ocelot.opendevices.core.render.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.TextureUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a color texture buffer.</p>
 *
 * @author Ocelot
 */
public class AdvancedFboAttachmentColorTexture2D implements AdvancedFboTextureAttachment
{
    private int id;
    private final int width;
    private final int height;
    private final int mipmapLevels;

    public AdvancedFboAttachmentColorTexture2D(int width, int height, int mipmapLevels)
    {
        this.id = -1;
        this.width = width;
        this.height = height;
        this.mipmapLevels = mipmapLevels;
    }

    @Override
    public void create()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.id = glGenTextures();
        TextureUtil.prepareImage(this.id, this.mipmapLevels, this.width, this.height);
    }

    @Override
    public void attach(int target, int attachment, int level)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        glFramebufferTexture2D(target, GL_COLOR_ATTACHMENT0 + attachment, GL_TEXTURE_2D, this.id, level);
    }

    @Override
    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    @Override
    public void bind()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(this.id);
    }

    @Override
    public void unbind()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(0);
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getSamples()
    {
        return 1;
    }

    @Override
    public boolean canSample()
    {
        return true;
    }

    @Override
    public void free()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (this.id != -1)
        {
            glDeleteTextures(this.id);
            this.id = -1;
        }
    }
}
