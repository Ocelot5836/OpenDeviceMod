package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.ProcessWindowRenderer;
import com.ocelot.opendevices.api.laptop.Computer;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;

public class TestProcessRenderer implements ProcessWindowRenderer<Computer, TestProcess>
{
    @Override
    public void render(TestProcess process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, TestProcess process, Window window, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        if (window.isWithinContent(mouseX - posX, mouseY - posY, partialTicks))
        {
            renderer.renderTooltip("dik", mouseX, mouseY);
        }
    }
}
