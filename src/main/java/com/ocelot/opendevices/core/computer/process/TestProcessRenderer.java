package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.device.ApplicationWindowRenderer;
import com.ocelot.opendevices.api.util.TooltipRenderer;

public class TestProcessRenderer extends ApplicationWindowRenderer<Computer, TestProcess>
{
    @Override
    public void render(TestProcess application, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        super.render(application, window, posX, posY, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, TestProcess application, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        super.renderOverlay(renderer, application, window, posX, posY, mouseX, mouseY, partialTicks);
    }
}
