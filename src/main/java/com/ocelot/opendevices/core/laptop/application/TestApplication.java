package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.TextComponent;
import com.ocelot.opendevices.api.laptop.application.Application;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    private Layout layoutTest;
    private TextComponent testText;

    public TestApplication()
    {
        //        this.layoutTest = new Layout(DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT);
        this.layoutTest = new Layout();

        this.testText = new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, Minecraft.getInstance().player.getDisplayName());
        this.testText.setClickListener((textComponent, mouseX, mouseY, mouseButton) -> this.getWindow().getLaptop().execute(() -> this.testText.setFontRenderer(this.testText.getFontRenderer() == Minecraft.getInstance().fontRenderer ? Minecraft.standardGalacticFontRenderer : Minecraft.DEFAULT_FONT_RENDERER_NAME)));
        this.layoutTest.addComponent(this.testText);

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
