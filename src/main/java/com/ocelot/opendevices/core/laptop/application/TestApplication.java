package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.laptop.application.Application;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    private Layout layoutTest;
    private Layout layoutTest2;
    private long creationTime;

    public TestApplication()
    {
        this.layoutTest = new Layout(50, 25);
        this.layoutTest2 = new Layout(200, 100);
        this.creationTime = -1;
    }

    @Override
    public void create()
    {
        this.setCurrentLayout(this.layoutTest);
        this.creationTime = System.currentTimeMillis() + 2000;
    }

    @Override
    public void update()
    {
        if (this.creationTime != -1 && System.currentTimeMillis() - this.creationTime > 0)
        {
            this.setCurrentLayout(this.layoutTest2);
            this.creationTime = -1;
        }
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.pushScissor(x, y, this.getWindow().getWidth() - 2, this.getWindow().getHeight() - 2 - DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("Actual Application Content", x, y, 0xffffffff);
        RenderUtil.popScissor();
    }
}
