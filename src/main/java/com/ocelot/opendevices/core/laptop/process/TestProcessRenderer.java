package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.ProcessWindowRenderer;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.util.TooltipRenderer;

public class TestProcessRenderer implements ProcessWindowRenderer<Computer, TestProcess>
{
    @Override
    public void render(TestProcess process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Computer computer = process.getDevice();
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, TestProcess process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Computer computer = process.getDevice();
    }
}
