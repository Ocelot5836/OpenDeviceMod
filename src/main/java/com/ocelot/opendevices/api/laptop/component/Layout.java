package com.ocelot.opendevices.api.laptop.component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Layout implements Component
{
    private float x;
    private float y;
    private float width;
    private float height;

    @Override
    public void update()
    {

    }

    @Override
    public void render(float partialTicks)
    {

    }

    @Override
    public float getX()
    {
        return 0;
    }

    @Override
    public float getY()
    {
        return 0;
    }

    @Override
    public float getWidth()
    {
        return 0;
    }

    @Override
    public float getHeight()
    {
        return 0;
    }
}
