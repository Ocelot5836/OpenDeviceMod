package com.ocelot.opendevices.api.application;

import com.ocelot.opendevices.core.computer.ApplicationInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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
    AppInfo EMPTY = new ApplicationInfo(new StringTextComponent("Missing Name"), new StringTextComponent("Missing Description"), new ITextComponent[0], "Missing Version");

    /**
     * @return The name of the application
     */
    ITextComponent getName();

    /**
     * @return A description of what the application does
     */
    ITextComponent getDescription();

    /**
     * @return The people that created the application
     */
    ITextComponent[] getAuthors();

    /**
     * @return The current version of the application
     */
    String getVersion();
}
