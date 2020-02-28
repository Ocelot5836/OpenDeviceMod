package com.ocelot.opendevices.api.laptop.application;

import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Computer;

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
    @Nullable
    UUID getLayout(UUID windowId);

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