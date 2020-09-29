package com.ocelot.opendevices.api.computer.application;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.process.DeviceProcess;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Specifies that a {@link DeviceProcess} is also an Application. Applications have extra required information that allows the {@link Device} running this to determine extra information.</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
public interface Application<T extends Device> extends DeviceProcess<T>
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
}