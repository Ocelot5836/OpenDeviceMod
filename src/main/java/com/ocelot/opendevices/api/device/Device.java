package com.ocelot.opendevices.api.device;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IWorld;

import java.util.UUID;

/**
 * <p>A device is a {@link TileEntity} that has the capability to interact with the Device API.</p>
 * <p>Devices can communicate with other devices based on their ID as well.</p>
 *
 * @author Ocelot
 */
public interface Device
{
    /**
     * @return The world the laptop is in
     */
    IWorld getWorld();

    /**
     * @return The unique address id of this device used for communication
     */
    UUID getAddress();

    /**
     * @return Whether or not the laptop is currently in a client world
     */
    default boolean isClient()
    {
        return this.getWorld() != null && this.getWorld().isRemote();
    }
}
