package com.ocelot.opendevices.api.util.icon;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * <p>Determines a single icon that can be fetched from a grid.</p>
 *
 * @author MrCrayfish
 */
public interface IIcon
{
    /**
     * @return The location of the icon grid texture
     */
    ResourceLocation getIconLocation();

    /**
     * @return The x coordinate on the texture this icon uses
     */
    int getU();

    /**
     * @return The y coordinate on the texture this icon uses
     */
    int getV();

    /**
     * @return The size of this icon
     */
    int getIconSize();

    /**
     * @return The amount of icons in the x
     */
    int getGridWidth();

    /**
     * @return The amount of icons in the y
     */
    int getGridHeight();

    /**
     * @return The width of the texture image
     */
    default int getTextureWidth()
    {
        return this.getGridWidth() * this.getIconSize();
    }

    /**
     * @return The height of the texture image
     */
    default int getTextureHeight()
    {
        return this.getGridHeight() * this.getIconSize();
    }

    /**
     * @return The index of this icon
     */
    int ordinal();

    /**
     * Renders this icon at the specified x and y.
     *
     * @param x The x to render the icon at
     * @param y The y to render the icon at
     */
    default void render(float x, float y)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getIconLocation());
        int size = this.getIconSize();
        RenderUtil.drawRectWithTexture(x, y, this.getU(), this.getV(), size, size, size, size, this.getTextureWidth(), this.getTextureHeight());
    }
}
