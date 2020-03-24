package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.api.component.ButtonComponent;
import com.ocelot.opendevices.api.component.ImageComponent;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.util.icon.Alphabet;
import net.minecraft.util.text.StringTextComponent;

import java.util.Locale;
import java.util.function.Function;

public class TestProcessLayoutSupplier implements Function<Integer, Layout>
{
    private TestProcess process;

    public TestProcessLayoutSupplier(TestProcess process)
    {
        this.process = process;
    }

    @Override
    public Layout apply(Integer id)
    {
        Layout layout = new Layout();
        String url = id == 0 ? "https://cdn.discordapp.com/attachments/447419834632896512/692016522578624592/unknown.png" : "https://api.battlefieldsmc.net/api/launcher/images/welcome.png";
        layout.addComponent(new ImageComponent(0, 0, layout.getWidth(), layout.getHeight(), ImageComponent.with(url)));
        Alphabet[] characters = Alphabet.getCharSequence(url.toUpperCase(Locale.ROOT));
        int xOffset = 0;
        int yOffset = 0;
        for (Alphabet character : characters)
        {
            if (xOffset + character.getWidth() > layout.getWidth())
            {
                xOffset = 0;
                yOffset += character.getHeight();
            }
            layout.addComponent(new ImageComponent(xOffset, yOffset, character.getWidth(), character.getHeight(), ImageComponent.with(character)));
            xOffset += character.getWidth();
        }
        //        SpinnerComponent spinner = new SpinnerComponent(64, 64);
        //        layout.addComponent(spinner);
        //        layout.addComponent(new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(Blocks.DIAMOND_BLOCK).getTextComponent()).setClickListener((text, mouseX, mouseY, mouseButton) ->
        //        {
        //            spinner.setPaused(!spinner.isPaused());
        //            return true;
        //        }));
        ButtonComponent button = new ButtonComponent(70, 70).setText(new StringTextComponent("Hello, World!"));
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0)
            {
                this.process.getWindow().requestClose();
                return true;
            }
            return false;
        });
        layout.addComponent(button);
        //                for (int i = 0; i < Icons.values().length; i++)
        //                {
        //                    int x = i % 12;
        //                    int y = i / 12;
        //                    layout.addComponent(new ImageComponent(80 + x * 10, 10 + y * 10, 10, 10, ImageComponent.with(Icons.values()[i])));
        //                }
        return layout;
    }
}
