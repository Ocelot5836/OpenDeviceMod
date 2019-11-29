package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.TextComponent;
import com.ocelot.opendevices.api.laptop.application.Application;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

// TODO move to an example mod
@Application.Register(OpenDevices.MOD_ID + ":test")
public class TestApplication extends Application
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "test");

    private Layout layoutTest;

    public TestApplication()
    {
        //        this.layoutTest = new Layout(DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT);
        this.layoutTest = new Layout();

        int i = 0;
        for (Item item : ForgeRegistries.ITEMS)
        {
            this.layoutTest.addComponent(new TextComponent((i / 21) * 75, (i % 21) * 8, Minecraft.DEFAULT_FONT_RENDERER_NAME, new ItemStack(item).getTextComponent()));
            i++;
        }
    }

    @Override
    public void create()
    {
        this.setCurrentLayout(this.layoutTest);
    }
}
