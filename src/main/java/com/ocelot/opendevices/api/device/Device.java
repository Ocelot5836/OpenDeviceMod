package com.ocelot.opendevices.api.device;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Collection;
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
     * Syncs the process with the specified id to the server and all clients.
     *
     * @param processId The if of the process to sync
     */
    void syncProcess(UUID processId);

    /**
     * Crates and starts a new process.
     *
     * @param processId The id of the process to start
     * @return The id assigned to the process
     * @throws IllegalArgumentException If the process registered under that id is either null or not for this device
     */
    UUID executeProcess(ResourceLocation processId);

    /**
     * @return The world the laptop is in
     */
    IWorld getWorld();

    /**
     * @return The unique address id of this device used for communication
     */
    UUID getAddress();

    /**
     * @return The processes that are currently being executed
     */
    Collection<UUID> getProcessIds();

    /**
     * Checks the currently running processes for the specified id.
     *
     * @param id The id of the process to fetch
     * @return The process found or null if there is no process with that id
     */
    @Nullable
    DeviceProcess<? extends Device> getProcess(UUID id);

    /**
     * @return Whether or not the laptop is currently in a client world
     */
    default boolean isClient()
    {
        return this.getWorld() != null && this.getWorld().isRemote();
    }
}
