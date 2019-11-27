package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.laptop.application.Application;
import com.ocelot.opendevices.api.laptop.application.LayoutProtocol;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    public TestApplication()
    {
    }

    @Override
    public void init(@Nullable CompoundNBT data)
    {
        this.setCurrentLayout(new Layout(80, 100), LayoutProtocol.NOTHING);
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.pushScissor(x, y, this.getWindow().getWidth() - 2, this.getWindow().getHeight() - 2 - DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("Actual Application Content", x, y, 0xffffffff);
        RenderUtil.popScissor();
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
