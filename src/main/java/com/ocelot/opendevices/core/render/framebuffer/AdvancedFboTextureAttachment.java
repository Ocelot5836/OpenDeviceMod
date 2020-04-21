package com.ocelot.opendevices.core.render.framebuffer;

/**
 * <p>A texture attachment added to an {@link AdvancedFbo}</p>
 *
 * @author Ocelot
 */
public interface AdvancedFboTextureAttachment extends AdvancedFboAttachment
{
    @Override
    default void attach(int target, int attachment)
    {
        this.attach(target, attachment, 0);
    }

    /**
     * Attaches this attachment to the provided target under the specified attachment id.
     *
     * @param target     The target to attach this attachment to
     * @param attachment The attachment to attach this attachment under
     * @param level      The mipmap level to attach
     */
    void attach(int target, int attachment, int level);

    /**
     * @return The mipmap levels in this attachment
     */
    int getMipmapLevels();
}
