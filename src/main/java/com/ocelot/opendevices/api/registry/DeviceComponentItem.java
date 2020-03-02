package com.ocelot.opendevices.api.registry;

/**
 * <p>Used to determine certain characteristics about a component.</p>
 *
 * @author Ocelot
 */
public interface DeviceComponentItem
{
    /**
     * @return The tier level of this component or -1 if this component has no tier. In the base mod there is only 0, 1, and 2
     */
    int getTier();
}
