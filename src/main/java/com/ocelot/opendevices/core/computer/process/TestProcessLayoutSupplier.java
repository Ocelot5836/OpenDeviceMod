package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.*;
import com.ocelot.opendevices.api.util.WindowLayoutManager;
import com.ocelot.opendevices.api.util.icon.Alphabet;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.Locale;
import java.util.function.Function;

public class TestProcessLayoutSupplier implements WindowLayoutManager.LayoutProvider
{
    private TestProcess process;

    public TestProcessLayoutSupplier(TestProcess process)
    {
        this.process = process;
    }

    @Override
    public Layout create(int id)
    {
        Layout layout1 = new Layout();
        for (int i = 0; i < 2; i++)
        {
            Layout layout = new ScrollableLayout(i * DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH / 2f, 0, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH / 2, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT * 2, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT);
            String url = i == 0 ? "https://cdn.discordapp.com/attachments/447419834632896512/692016522578624592/unknown.png" : "https://pluspng.com/img-png/window-hd-png-open-window-png-400.png";
            if(i == 0)
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
            if(i == 1)
                layout.addComponent(new ImageComponent(0, 0, layout.getWidth(), layout.getHeight(), ImageComponent.with(url)));
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
            layout1.addComponent(layout);
        }
        return layout1;
    }
}
