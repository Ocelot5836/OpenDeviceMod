package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;

/**
 * <p>Contains static values that are used by core classes.</p>
 *
 * @author Ocelot
 */
public class DeviceConstants
{
    private DeviceConstants() {}

    public static final boolean DEVELOPER_MODE = true;
    public static final int MAX_OPEN_APPS = 5;

    public static final ResourceLocation LAPTOP_GUI = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/laptop.png");
    public static final int LAPTOP_GUI_BORDER = 10;
    public static final int LAPTOP_GUI_WIDTH = 384;
    public static final int LAPTOP_GUI_HEIGHT = 216;

    public static final int LAPTOP_SCREEN_WIDTH = LAPTOP_GUI_WIDTH - LAPTOP_GUI_BORDER * 2;
    public static final int LAPTOP_SCREEN_HEIGHT = LAPTOP_GUI_HEIGHT - LAPTOP_GUI_BORDER * 2;
    public static final int LAPTOP_WINDOW_BUTTON_SIZE = 11;
    public static final int LAPTOP_WINDOW_BAR_HEIGHT = 12;

    public static final int LAPTOP_DEFAULT_APPLICATION_WIDTH = 200;
    public static final int LAPTOP_DEFAULT_APPLICATION_HEIGHT = 100;
    public static final int LAPTOP_MIN_APPLICATION_WIDTH = 13;
    public static final int LAPTOP_MIN_APPLICATION_HEIGHT = 1;
    public static final int LAPTOP_MAX_APPLICATION_WIDTH = LAPTOP_SCREEN_WIDTH - 2;
    public static final int LAPTOP_MAX_APPLICATION_HEIGHT = LAPTOP_SCREEN_HEIGHT - LAPTOP_WINDOW_BAR_HEIGHT - 2;

    public static final float LAPTOP_OPENED_ANGLE = 102;
    public static final float LAPTOP_TE_SCREEN_WIDTH = 12 * 0.0625f;
    public static final float LAPTOP_TE_SCREEN_HEIGHT = ((float) LAPTOP_SCREEN_HEIGHT / (float) LAPTOP_SCREEN_WIDTH) * LAPTOP_TE_SCREEN_WIDTH;

    public static final ResourceLocation DEFAULT_BACKGROUND_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/laptop/default_background.png");
    public static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/laptop/window.png");
    public static final ResourceLocation COMPONENTS_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/laptop/components.png");

}
