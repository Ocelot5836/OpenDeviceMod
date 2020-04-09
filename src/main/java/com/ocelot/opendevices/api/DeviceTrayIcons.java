package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TrayItem;
import com.ocelot.opendevices.core.computer.taskbar.DeviceTrayIconsImpl;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * <p>Contains tray icons that are used by the base mod content.</p>
 *
 * @author Ocelot
 * @see TrayItem
 */
public class DeviceTrayIcons
{
    public static final ResourceLocation TEST = new ResourceLocation(OpenDevices.MOD_ID, "test");

    @TrayItem.Register(OpenDevices.MOD_ID + ":test")
    public static final Function<Computer, Boolean> TEST_ICON = DeviceTrayIconsImpl::executeTest;
}
