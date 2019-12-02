package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.settings.BooleanLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.DoubleLaptopSetting;
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
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":scroll_speed")
    public static final LaptopSetting<Double> SCROLL_SPEED = new DoubleLaptopSetting(8.0);

    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_disabled_color")
    public static final LaptopSetting<Integer> BUTTON_DISABLED_COLOR = new IntegerLaptopSetting(0xFFFFFFFF);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_color")
    public static final LaptopSetting<Integer> BUTTON_COLOR = new IntegerLaptopSetting(0xFFFFFFFF);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_highlight_color")
    public static final LaptopSetting<Integer> BUTTON_HIGHLIGHT_COLOR = new IntegerLaptopSetting(0xFFFFFFFF);

    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_text_disabled_color")
    public static final LaptopSetting<Integer> BUTTON_TEXT_DISABLED_COLOR = new IntegerLaptopSetting(0xFFA0A0A0);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_text_color")
    public static final LaptopSetting<Integer> BUTTON_TEXT_COLOR = new IntegerLaptopSetting(0xFFE0E0E0);
    @LaptopSetting.Register(OpenDevices.MOD_ID + ":button_text_highlight_color")
    public static final LaptopSetting<Integer> BUTTON_TEXT_HIGHLIGHT_COLOR = new IntegerLaptopSetting(0xFFFFFFA0);

    @LaptopSetting.Register(OpenDevices.MOD_ID + ":menu_bar_color")
    public static final LaptopSetting<Integer> MENU_BAR_COLOR = new IntegerLaptopSetting(0xFF36393C);
}