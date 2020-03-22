package com.ocelot.opendevices.api.util.icon;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
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
     * @return The x size of this icon
     */
    int getWidth();

    /**
     * @return The y size of this icon
     */
    int getHeight();

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
    default int getSourceWidth()
    {
        return this.getGridWidth() * this.getWidth();
    }

    /**
     * @return The height of the texture image
     */
    default int getSourceHeight()
    {
        return this.getGridHeight() * this.getHeight();
    }

    /**
     * @return The index of this icon
     */
    int getIndex();

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
        RenderUtil.drawRectWithTexture(x, y, this.getU(), this.getV(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getSourceWidth(), this.getSourceHeight());
    }

    /**
     * Serializes the icon to NBT.
     *
     * @param icon The icon to serialize
     * @return The tag full of data
     */
    static CompoundNBT serializeNBT(IIcon icon)
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("index", icon.getIndex());
        nbt.putString("class", icon.getClass().getName());
        return nbt;
    }

    /**
     * Deserializes an icon from NBT.
     *
     * @param nbt The tag full of data
     * @return The icon serialized or {@link MissingIcon#INSTANCE} if there was an error
     */
    static IIcon deserializeNBT(CompoundNBT nbt)
    {
        int index = nbt.getInt("index");
        String className = nbt.getString("class");
        try
        {
            Class<?> clazz = Class.forName(className);
            if (!clazz.isEnum())
                throw new IllegalArgumentException("Icon class '" + className + "' is not an enum.");
            if (!IIcon.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException("Icon enum '" + className + "' does not implement IIcon.");

            IIcon[] icons = (IIcon[]) clazz.getEnumConstants();
            if (index < 0 || index >= icons.length)
            {
                OpenDevices.LOGGER.warn("Icon index '" + index + "' is out of bounds for enum '" + className + "'. Using first element.");
                index = 0;
            }

            if (icons.length == 0)
            {
                OpenDevices.LOGGER.warn("No icons could be found for enum '" + className + "'. Using missing icon.");
                return MissingIcon.INSTANCE;
            }

            return (IIcon) clazz.getEnumConstants()[index];
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not read IIcon from NBT for '" + className + "' with id '" + index + "'. Using missing icon.", e);
        }

        return MissingIcon.INSTANCE;
    }
}
