package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.settings.BooleanLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.IntegerLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import net.minecraft.util.ResourceLocation;

/**
 * <p>Contains settings that come by default.</p>
 *
 * @author Ocelot
 */
public class LaptopSettings
{
    private LaptopSettings() {}

    @LaptopSetting.Register
    public static final LaptopSetting<Integer> DESKTOP_TEXT_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "desktop_text_color"), 0xFFFFFFFF);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> TASKBAR_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "taskbar_color"), 0x45494D);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> TASKBAR_HIGHLIGHT_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "taskbar_highlight_color"), 0xBDC6FF);
    @LaptopSetting.Register
    public static final LaptopSetting<Boolean> TASKBAR_ENLARGED = new BooleanLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "taskbar_enlarged"), false);

    @LaptopSetting.Register
    public static final LaptopSetting<Integer> WINDOW_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "window_color"), 0x45494D);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> FOCUSED_WINDOW_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "focused_window_color"), 0xFF00FF);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> WINDOW_BUTTON_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "window_button_color"), 0xCCCCCC);

}
