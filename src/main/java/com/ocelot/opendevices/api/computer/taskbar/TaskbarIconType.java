package com.ocelot.opendevices.api.computer.taskbar;

import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.core.computer.taskbar.ApplicationTaskbarIcon;
import com.ocelot.opendevices.core.computer.taskbar.WindowTaskbarIcon;

import java.util.function.Function;

/**
 * <p>Helpful enum for the different types of icons that exist.</p>
 *
 * @author Ocelot
 */
public enum TaskbarIconType implements Function<Computer, TaskbarIcon>
{
    APPLICATION(ApplicationTaskbarIcon::new), WINDOW(WindowTaskbarIcon::new);

    private Function<Computer, TaskbarIcon> creator;

    TaskbarIconType(Function<Computer, TaskbarIcon> creator)
    {
        this.creator = creator;
    }

    @Override
    public TaskbarIcon apply(Computer computer)
    {
        return this.creator.apply(computer);
    }

    /**
     * @return The id of this state as a byte
     */
    public byte serialize()
    {
        return (byte) this.ordinal();
    }

    /**
     * Finds a value for the provided id.
     *
     * @param id The id provided by {@link #serialize()}
     * @return The value corresponding with that id
     */
    public static TaskbarIconType deserialize(int id)
    {
        return id < 0 || id >= values().length ? APPLICATION : values()[id];
    }
}
