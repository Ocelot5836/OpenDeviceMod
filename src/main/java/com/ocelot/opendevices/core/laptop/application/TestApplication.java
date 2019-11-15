package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.window.application.Application;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("Actual Application Content", x, y, 0xffffffff);
    }

    @Override
    public void renderOverlay(Screen screen, float x, float y, int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void save(CompoundNBT nbt)
    {
    }

    @Override
    public void load(CompoundNBT nbt)
    {
    }
}
