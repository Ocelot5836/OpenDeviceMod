package com.ocelot.opendevices.api.component;

/**
 * <p>Helpful enum for interactive {@link Component} states.</p>
 *
 * @author Ocelot
 */
public enum ButtonState
{
    VISIBLE, DISABLED, INVISIBLE;

    /**
     * @return The id of this state as a byte
     */
    public byte serialize()
    {
        return (byte) this.ordinal();
    }

    /**
     * Finds a button state for the provided id.
     *
     * @param id The id provided by {@link #serialize()}
     * @return The state corresponding with that id
     */
    public static ButtonState deserialize(int id)
    {
        return values()[id % values().length];
    }
}
