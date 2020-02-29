package com.ocelot.opendevices.api.computer.application;

import com.ocelot.opendevices.core.computer.application.ApplicationInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * <p>Contains information about each application that is registered. Application info be queried using {@link ApplicationManager#getAppInfo(Class)} or {@link ApplicationManager#getAppInfo(ResourceLocation)}</p>
 *
 * @author Ocelot
 * @see Application
 * @see ApplicationManager
 */
public interface AppInfo
{
    /**
     * Application info that is used when there is an error fetching info
     */
    AppInfo EMPTY = new ApplicationInfo("Missing Name", "Missing Description", new String[0], "Missing Version", false);

    /**
     * @return The name of the application
     */
    String getName();

    /**
     * @return A description of what the application does
     */
    String getDescription();

    /**
     * @return The people that created the application
     */
    String[] getAuthors();

    /**
     * @return The current version of the application
     */
    String getVersion();

    /**
     * @return Whether or not names should be formatted using {@link I18n}.
     */
    boolean shouldTranslate();
}
