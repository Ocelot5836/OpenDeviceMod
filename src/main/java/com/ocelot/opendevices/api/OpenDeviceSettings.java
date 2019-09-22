package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.api.device.laptop.settings.ResourceLocationLaptopSetting;
import com.ocelot.opendevices.api.device.laptop.settings.SettingsManager;
import com.ocelot.opendevices.api.device.laptop.settings.StringLaptopSetting;
import net.minecraft.util.ResourceLocation;

public class OpenDeviceSettings
{
    @SettingsManager.Register
    public static final LaptopSetting<ResourceLocation> DESKTOP_BACKGROUND = new ResourceLocationLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "desktopBackground"), new ResourceLocation(OpenDevices.MOD_ID, "textures/background/default.png"));
}
