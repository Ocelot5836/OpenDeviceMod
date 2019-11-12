package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all client information about applications.
 *
 * @author Ocelot
 */
public class LaptopClientInfo
{
    static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();

    /**
     * Reloads all applications
     */
    static void reloadApps(){
        OpenDevices.LOGGER.info("Loading \'" + APP_INFO.size() + "\' app(s).");
        APP_INFO.values().forEach(info ->
        {
            try
            {
                info.reload();
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not load all app info!", e);
            }
        });
    }

    /**
     * Checks the registry for app info under the specified registry name.
     *
     * @param registryName The name of the application to get the info of
     * @return The application info of that app
     */
    public static AppInfo getAppInfo(ResourceLocation registryName)
    {
        if (!APP_INFO.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        return APP_INFO.get(registryName);
    }
}
