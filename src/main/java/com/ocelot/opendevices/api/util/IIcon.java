package com.ocelot.opendevices.api.util;

import com.mojang.blaze3d.platform.GlStateManager;
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
     * @return The size of this icon
     */
    int getIconSize();

    /**
     * @return The amount of icons the grid has in the x
     */
    int getGridWidth();

    /**
     * @return The amount of icons the grid has in the y
     */
    int getGridHeight();

    /**
     * @return The width of the source texture in pixels
     */
    int getTextureWidth();

    /**
     * @return The height of the source texture in pixels
     */
    int getTextureHeight();

    /**
     * @return The x coordinate on the texture this icon uses
     */
    int getU();

    /**
     * @return The y coordinate on the texture this icon uses
     */
    int getV();

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
        int assetWidth = this.getGridWidth() * size;
        int assetHeight = this.getGridHeight() * size;
        RenderUtil.drawRectWithTexture(x, y, this.getU(), this.getV(), size, size, size, size, assetWidth, assetHeight);
    }
}
