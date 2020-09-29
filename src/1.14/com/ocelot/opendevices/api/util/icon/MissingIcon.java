package com.ocelot.opendevices.api.util.icon;

import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.ResourceLocation;

/**
 * <p>The missing icon is an {@link IIcon} that can be used in place of corrupted icons.</p>
 *
 * @author Ocelot
 */
public enum MissingIcon implements IIcon
{
    INSTANCE;

    @Override
    public ResourceLocation getIconLocation()
    {
        return MissingTextureSprite.getLocation();
    }

    @Override
    public int getU()
    {
        return 0;
    }

    @Override
    public int getV()
    {
        return 0;
    }

    @Override
    public int getWidth()
    {
        return 16;
    }

    @Override
    public int getHeight()
    {
        return 16;
    }

    @Override
    public int getGridWidth()
    {
        return 1;
    }

    @Override
    public int getGridHeight()
    {
        return 1;
    }

    @Override
    public int getIndex()
    {
        return 0;
    }
}
