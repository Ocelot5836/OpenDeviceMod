package com.ocelot.opendevices.api.util;

/**
 * <p>Helpful enum for how images fit to a screen.</p>
 *
 * @author Ocelot
 */
public enum ImageFit
{
    FILL, STRETCH, TILE, CENTER, SPAN;

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
    public static ImageFit deserialize(int id)
    {
        return id < 0 || id >= values().length ? FILL : values()[id];
    }
}
