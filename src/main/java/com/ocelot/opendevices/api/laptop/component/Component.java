package com.ocelot.opendevices.api.laptop.component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Component
{
    void update();

    void render(float partialTicks);

    float getX();

    float getY();

    float getWidth();

    float getHeight();
}
