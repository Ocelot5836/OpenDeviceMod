package com.ocelot.opendevices.core.render.framebuffer;

import com.mojang.blaze3d.systems.RenderSystem;
import org.apache.commons.lang3.Validate;

import static org.lwjgl.opengl.GL30.*;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a color render buffer.</p>
 *
 * @author Ocelot
 */
public class AdvancedFboAttachmentColorRenderBuffer implements AdvancedFboAttachment
{
    private int id;
    private final int width;
    private final int height;
    private final int samples;

    public AdvancedFboAttachmentColorRenderBuffer(int width, int height, int samples)
    {
        this.id = -1;
        this.width = width;
        this.height = height;
        Validate.inclusiveBetween(1, glGetInteger(GL_MAX_SAMPLES), samples);
        this.samples = samples;
    }

    @Override
    public void create()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.id = glGenRenderbuffers();
        this.bind();
        if (this.samples == 1)
        {
            glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, this.width, this.height);
        }
        else
        {
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, this.samples, GL_RGBA8, this.width, this.height);
        }
        this.unbind();
    }

    @Override
    public void attach(int target, int attachment)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        glFramebufferRenderbuffer(target, GL_COLOR_ATTACHMENT0+attachment, GL_RENDERBUFFER, this.id);
    }

    @Override
    public void bind()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        glBindRenderbuffer(GL_RENDERBUFFER, this.id);
    }

    @Override
    public void unbind()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
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
        return samples;
    }

    @Override
    public boolean canSample()
    {
        return false;
    }

    @Override
    public void free()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (this.id != -1)
        {
            glDeleteRenderbuffers(this.id);
            this.id = -1;
        }
    }
}
