package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.api.component.ImageComponent;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.SpinnerComponent;
import com.ocelot.opendevices.api.component.TextComponent;
import com.ocelot.opendevices.api.util.ImageFit;
import com.ocelot.opendevices.api.util.icon.Alphabet;
import com.ocelot.opendevices.api.util.icon.Icons;
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
//        SpinnerComponent spinner = new SpinnerComponent(64, 64);
//        layout.addComponent(spinner);
//        layout.addComponent(new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(Blocks.DIAMOND_BLOCK).getTextComponent()).setRenderShadow(true).setClickListener((text, mouseX, mouseY, mouseButton) ->
//        {
//            spinner.setPaused(!spinner.isPaused());
//            return true;
//        }));
        String url = id == 0 ? "https://cdn.discordapp.com/attachments/426584849088774187/690434011415183420/2020-03-19_22.37.18.png" : "https://api.battlefieldsmc.net/api/launcher/images/welcome.png";
//        layout.addComponent(new ImageComponent(0, 20, 64, 64, ImageComponent.with(url)).setImageFit(ImageFit.STRETCH));
        layout.addComponent(new ImageComponent(0, 0, layout.getWidth(), layout.getHeight(), ImageComponent.with(Alphabet.UPPERCASE_Q)).setImageFit(ImageFit.TILE));
        //        for (int i = 0; i < Icons.values().length; i++)
//        {
//            int x = i % 12;
//            int y = i / 12;
//            layout.addComponent(new ImageComponent(80 + x * 10, 10 + y * 10, 10, 10, ImageComponent.with(Icons.values()[i])));
//        }
        return layout;
    }
}
