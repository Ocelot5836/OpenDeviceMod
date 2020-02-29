package com.ocelot.opendevices.api.computer.application;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.device.DeviceProcess;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * <p>Specifies that a {@link DeviceProcess} is also an Application. Applications have extra required information that allows the {@link Computer} to determine extra features.</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
public interface Application
{
    /**
     * Checks for a layout in the specified window.
     *
     * @param windowId The id of the window to check
     * @return The currently displaying layout or null if there is no layout to render
     */
    @Nullable
    default Layout getLayout(UUID windowId)
    {
        return null;
    }

    /**
     * @return The {@link AppInfo} associated with this application or {@link AppInfo#EMPTY} if there is no info about this app
     */
    default AppInfo getInfo()
    {
        return ApplicationManager.getAppInfo(this.getClass());
    }

    /**
     * Registers a new type of application for the {@link Computer}. Allows the detection of a {@link DeviceProcess} as actually being an application.
     *
     * @author Ocelot
     * @see Computer
     */
    @Target(ElementType.TYPE)
    @interface Register
    {
    }
}