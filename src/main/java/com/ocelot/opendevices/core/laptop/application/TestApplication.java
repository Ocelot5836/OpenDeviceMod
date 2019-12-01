package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.ButtonComponent;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.TextComponent;
import com.ocelot.opendevices.api.laptop.application.Application;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    private Layout layoutTest;

    public TestApplication()
    {
        this.layoutTest = new Layout();

        TextComponent testText = new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, Minecraft.getInstance().player.getDisplayName().appendSibling(new ItemStack(Blocks.DIAMOND_BLOCK).getTextComponent()));
        testText.setClickListener((textComponent, mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0)
            {
                this.getWindow().getLaptop().execute(() -> testText.setFontRenderer(testText.getFontRenderer() == Minecraft.getInstance().fontRenderer ? Minecraft.standardGalacticFontRenderer : Minecraft.DEFAULT_FONT_RENDERER_NAME));
                return true;
            }
            return false;
        });
        this.layoutTest.addComponent(testText);

        ButtonComponent testButton = new ButtonComponent(0, 16);
        testButton.setText(new StringTextComponent("Press Me!").setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new StringTextComponent("Line 1 of the tooltip ")
                        .appendSibling(Minecraft.getInstance().player.getDisplayName())
                        .appendSibling(new StringTextComponent(" Some colored text after the player name").setStyle(new Style().setColor(TextFormatting.AQUA)))
                        .appendSibling(new StringTextComponent(" Testing color overflow"))
                        .appendSibling(new StringTextComponent(" Seems to work fine to me, and the tooltip wraps after a little bit too! Testing new line character\njust before this\nmight have worked!").setStyle(new Style().setColor(TextFormatting.GOLD))))))
        );
        this.layoutTest.addComponent(testButton);

        //        int i = 1;
        //        for (Item item : ForgeRegistries.ITEMS)
        //        {
        //            TextComponent text = new TextComponent((i / 21) * 75, (i % 21) * 9, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(item).getTextComponent());
        //            text.setClickListener((textComponent, mouseX, mouseY, mouseButton) -> this.getWindow().getLaptop().execute(() -> text.setFontRenderer(this.testText.getFontRenderer() == Minecraft.getInstance().fontRenderer ? Minecraft.standardGalacticFontRenderer : Minecraft.DEFAULT_FONT_RENDERER_NAME)));
        //            this.layoutTest.addComponent(text);
        //            i++;
        //        }
    }

    @Override
    public void create()
    {
        this.setCurrentLayout(this.layoutTest);
    }
}
