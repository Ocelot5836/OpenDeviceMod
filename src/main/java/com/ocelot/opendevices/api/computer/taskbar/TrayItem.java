package com.ocelot.opendevices.api.computer.taskbar;

import com.ocelot.opendevices.api.application.ApplicationManager;
import com.ocelot.opendevices.api.component.Layout;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * <p>An icon that can be clicked and displays a layout on the right side of the task bar.</p>
 *
 * @author Ocelot
 */
public interface TrayItem
{
    /**
     * @return The id of this icon
     */
    UUID getId();

    /**
     * @return The registry name of this icon
     */
    ResourceLocation getRegistryName();

    /**
     * @return The info of this icon
     */
    default TrayItemInfo getInfo()
    {
        return ApplicationManager.getTrayIconInfo(this.getRegistryName());
    }

    /**
     * Registers a new type of tray icon for a device.
     *
     * @author Ocelot
     */
    @Target(ElementType.FIELD)
    @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>.
         */
        String value();
    }
}
