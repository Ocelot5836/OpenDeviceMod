package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.window.application.Application;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("Actual Application Content", x, y, 0xffffffff);
    }

    @Override
    public String getTitle()
    {
        return super.getTitle();
    }

    @Override
    public TextureAtlasSprite getIconSprite()
    {
        return null;
    }
}
