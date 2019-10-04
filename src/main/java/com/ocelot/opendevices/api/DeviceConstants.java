package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.settings.IntegerLaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import net.minecraft.util.ResourceLocation;

/**
 * <p>Contains static values that are used by core classes.</p>
 *
 * @author Ocelot
 */
public class DeviceConstants
{
    public static final boolean DEVELOPER_MODE = true;
    public static final int MAX_OPEN_APPS = 5;

    public static final ResourceLocation LAPTOP_GUI = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/laptop.png");
    public static final int LAPTOP_GUI_BORDER = 10;
    public static final int LAPTOP_GUI_WIDTH = 384;
    public static final int LAPTOP_GUI_HEIGHT = 216;

    public static final int LAPTOP_SCREEN_WIDTH = LAPTOP_GUI_WIDTH - LAPTOP_GUI_BORDER * 2;
    public static final int LAPTOP_SCREEN_HEIGHT = LAPTOP_GUI_HEIGHT - LAPTOP_GUI_BORDER * 2;
    public static final int LAPTOP_TASK_BAR_HEIGHT = 18;
    public static final int LAPTOP_WINDOW_BUTTON_SIZE = 11;
    public static final int LAPTOP_WINDOW_BAR_HEIGHT = 12;

    public static final float LAPTOP_OPENED_ANGLE = 102;
    public static final float LAPTOP_TE_SCREEN_WIDTH = 12 * 0.0625f;
    public static final float LAPTOP_TE_SCREEN_HEIGHT = ((float) LAPTOP_SCREEN_HEIGHT / (float) LAPTOP_SCREEN_WIDTH) * LAPTOP_TE_SCREEN_WIDTH;

    public static final ResourceLocation DEFAULT_BACKGROUND_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/laptop/default_background.png");
    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/laptop/window.png");

    @LaptopSetting.Register
    public static final LaptopSetting<Integer> DESKTOP_TEXT_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "desktop_text_color"), 0xFFFFFFFF);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> TASKBAR_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "taskbar_color"), 0x45494D);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> TASKBAR_HIGHLIGHT_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "taskbar_highlight_color"), 0xBDC6FF);

    @LaptopSetting.Register
    public static final LaptopSetting<Integer> WINDOW_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "window_color"), 0x45494D);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> FOCUSED_WINDOW_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "focused_window_color"), 0xFF00FF);
    @LaptopSetting.Register
    public static final LaptopSetting<Integer> WINDOW_BUTTON_COLOR = new IntegerLaptopSetting(new ResourceLocation(OpenDevices.MOD_ID, "window_button_color"), 0xCCCCCC);

}
