package com.ocelot.opendevices.api.computer.taskbar;

import com.ocelot.opendevices.api.computer.Computer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Represents the most abstract form of a task bar for a {@link Computer}. Displays information such as tray items, pinned apps, etc.</p>
 *
 * @author Ocelot
 * @see Computer
 */
public interface TaskBar
{
    /**
     * Adds the specified icon to the tray
     *
     * @param icon The icon to add
     * @return The id of the created icon
     */
    UUID createTrayIcon(ResourceLocation icon);

    /**
     * Removes the specified icon from the tray
     *
     * @param id The id of the icon to remove
     */
    void removeTrayItem(UUID id);

    /**
     * @return The icons displayed on the task bar
     */
    TaskbarIcon[] getDisplayedIcons();

    /**
     * @return The items displayed in the tray
     */
    TrayItem[] getTrayItems();

    /**
     * @param id The id of the item to fetch
     * @return The tray item registered under the specified id
     */
    @Nullable
    TrayItem getTrayItem(UUID id);
}
