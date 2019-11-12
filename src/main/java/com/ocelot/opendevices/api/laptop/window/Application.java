package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.core.laptop.ApplicationManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

/**
 * <p>An application is a window that can be opened and used by the user via the {@link Desktop}.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public abstract class Application extends AbstractGui implements WindowContent
{
    /**
     * @return The registry name of this application
     */
    public ResourceLocation getRegistryName()
    {
        return ApplicationManager.getRegistryName(this.getClass());
    }

    /**
     * Registers a new type of window content that can be opened by the {@link Desktop} as a window.
     *
     * @author Ocelot
     * @see WindowContent
     */
    public @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>. <b><i>Will not register unless mod id is provided!</i></b>
         */
        String value();
    }
}
