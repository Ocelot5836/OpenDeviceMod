package com.ocelot.opendevices.api;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TrayItem;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * <p>Contains tray items that are used by the base mod content.</p>
 *
 * @author Ocelot
 * @see TrayItem
 */
public class DeviceTrayItems
{
    public static final ResourceLocation TEST = new ResourceLocation(OpenDevices.MOD_ID, "test");

    // TODO add abstract class with methods and registry names so this field can be used for opening tray items
    @TrayItem.Register(OpenDevices.MOD_ID + ":test")
    public static final Function<Computer, Boolean> TEST_ITEM = computer -> true;
}
