package com.ocelot.opendevices.api.laptop.application;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Computer;
import net.minecraft.util.ResourceLocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>Specifies that a {@link DeviceProcess} is also an Application. Applications have extra required information that allows the {@link Computer} to determine extra features.</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
public interface Application
{
    /**
     * @return The information associated with this application
     */
    default AppInfo getInfo()
    {
        ResourceLocation registryName = DeviceRegistries.getApplicationRegistryName(this.getClass());
        // TODO load info from /data/opendevices/...
        return AppInfo.EMPTY;
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