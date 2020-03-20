package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.SpinnerComponent;
import com.ocelot.opendevices.api.component.TextComponent;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class TestProcessLayoutSupplier implements Function<Integer, Layout>
{
    @Override
    public Layout apply(Integer id)
    {
        Layout layout = new Layout();
        SpinnerComponent spinner = new SpinnerComponent(64, 64);
        layout.addComponent(spinner);
        layout.addComponent(new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(Blocks.DIAMOND_BLOCK).getTextComponent()).setRenderShadow(true).setClickListener((text, mouseX, mouseY, mouseButton) ->
        {
            spinner.setPaused(!spinner.isPaused());
            return true;
        }));
        return layout;
    }
}
