package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.settings.BooleanLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.IntegerLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;

/**
 * <p>Contains settings that are used by the base mod content.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class LaptopSettings
{
    private LaptopSettings() {}

    @LaptopSetting.Register(OpenDevices.MOD_ID + ":desktop_text_color")
    public static final LaptopSetting<Integer> DESKTOP_TEXT_COLOR = new IntegerLaptopSetting(0xFFFFFFFF);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":taskbar_color")
    public static final LaptopSetting<Integer> TASKBAR_COLOR = new IntegerLaptopSetting(0x45494D);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":taskbar_highlight_color")
    public static final LaptopSetting<Integer> TASKBAR_HIGHLIGHT_COLOR = new IntegerLaptopSetting(0xBDC6FF);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":taskbar_enlarged")
    public static final LaptopSetting<Boolean> TASKBAR_ENLARGED = new BooleanLaptopSetting(false);

    @LaptopSetting.Register(OpenDevices.MOD_ID + ":window_color")
    public static final LaptopSetting<Integer> WINDOW_COLOR = new IntegerLaptopSetting(0x45494D);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":focused_window_color")
    public static final LaptopSetting<Integer> FOCUSED_WINDOW_COLOR = new IntegerLaptopSetting(0xFF00FF);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":window_button_color")
    public static final LaptopSetting<Integer> WINDOW_BUTTON_COLOR = new IntegerLaptopSetting(0xCCCCCC);
}