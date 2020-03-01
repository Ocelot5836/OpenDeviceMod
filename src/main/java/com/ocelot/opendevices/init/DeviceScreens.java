package com.ocelot.opendevices.init;

import com.ocelot.opendevices.container.ComponentBuilderContainer;
import com.ocelot.opendevices.container.ComponentBuilderScreen;
import net.minecraft.client.gui.ScreenManager;

public class DeviceScreens
{
    public static final ScreenManager.IScreenFactory<ComponentBuilderContainer, ComponentBuilderScreen> COMPONENT_BUILDER_SCREEN = ComponentBuilderScreen::new;

    public static void register()
    {
        ScreenManager.registerFactory(DeviceContainers.COMPONENT_BUILDER, COMPONENT_BUILDER_SCREEN);
    }
}