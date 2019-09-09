package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;

/**
 * Contains constants that are used by core classes.
 */
public class Constants
{
    public static final ResourceLocation LAPTOP_GUI = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/laptop.png");
    public static final int LAPTOP_BORDER = 10;
    public static final int LAPTOP_DEVICE_WIDTH = 384;
    public static final int LAPTOP_DEVICE_HEIGHT = 216;
    public static final int LAPTOP_SCREEN_WIDTH = LAPTOP_DEVICE_WIDTH - LAPTOP_BORDER * 2;
    public static final int LAPTOP_SCREEN_HEIGHT = LAPTOP_DEVICE_HEIGHT - LAPTOP_BORDER * 2;
}
