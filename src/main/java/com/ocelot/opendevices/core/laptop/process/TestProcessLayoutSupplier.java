package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.TextComponent;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Function;

public class TestProcessLayoutSupplier implements Function<Integer, Layout>
{
    @Override
    public Layout apply(Integer id)
    {
        Layout layout = new Layout();
        layout.addComponent(new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(Blocks.DIAMOND_BLOCK).getTextComponent()));
        return layout;
    }
}
