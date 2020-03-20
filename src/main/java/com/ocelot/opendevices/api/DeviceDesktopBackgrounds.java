package com.ocelot.opendevices.api;

import com.ocelot.opendevices.api.computer.desktop.LocalDesktopBackground;
import com.ocelot.opendevices.core.computer.desktop.LaptopLocalDesktopBackground;

import java.util.function.Supplier;

/**
 * <p>Contains static generators for the in-built desktop backgrounds.</p>
 *
 * @author Ocelot
 */
public class DeviceDesktopBackgrounds
{
    public static final Supplier<LocalDesktopBackground> DEFAULT = () -> new LaptopLocalDesktopBackground(DeviceConstants.DEFAULT_BACKGROUND_LOCATION, 0, 0, DeviceConstants.LAPTOP_GUI_WIDTH / 2f, DeviceConstants.LAPTOP_GUI_HEIGHT / 2f, DeviceConstants.LAPTOP_GUI_WIDTH / 2, DeviceConstants.LAPTOP_GUI_HEIGHT / 2);
}
