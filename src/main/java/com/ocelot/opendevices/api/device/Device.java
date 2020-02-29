package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.device.process.DeviceProcess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * <p>A device is a {@link TileEntity} that has the capability to interact with the Device API.</p>
 * <p>Devices can communicate with other devices based on their ID as well.</p>
 *
 * @author Ocelot
 */
public interface Device extends Executor
{
    @Override
    default void execute(Runnable command)
    {
        throw new UnsupportedOperationException("This device does not support command execution");
    }

    /**
     * Creates and starts a new process.
     *
     * @param processId The id of the process to start
     * @return The id assigned to the process
     * @throws IllegalArgumentException      If the process registered under that id is either null or not for this device
     * @throws UnsupportedOperationException If this devices does not support processes. Can be checked by using {@link #supportsProcesses()}
     */
    UUID executeProcess(ResourceLocation processId);

    /**
     * Stops the process with the specified ID and closes all associated tasks.
     *
     * @param processId The id of the process to stop
     * @throws UnsupportedOperationException If this devices does not support processes. Can be checked by using {@link #supportsProcesses()}
     */
    void terminateProcess(UUID processId);

    /**
     * Syncs the process with the specified id to the server and all clients. Does not sync the side called as it it assumed to already be in sync.
     *
     * @param processId The if of the process to sync
     * @throws UnsupportedOperationException If this devices does not support processes. Can be checked by using {@link #supportsProcesses()}
     */
    void syncProcess(UUID processId);

    /**
     * @return The world the laptop is in
     */
    IWorld getWorld();

    /**
     * @return The unique address id of this device. Used for communication between devices
     */
    UUID getAddress();

    /**
     * @return The processes that are currently being executed
     * @throws UnsupportedOperationException If this devices does not support processes. Can be checked by using {@link #supportsProcesses()}
     */
    Collection<UUID> getProcessIds();

    /**
     * Checks the currently running processes for the specified id.
     *
     * @param id The id of the process to fetch
     * @return The process found or null if there is no process with that id
     * @throws UnsupportedOperationException If this devices does not support processes. Can be checked by using {@link #supportsProcesses()}
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

    /**
     * @return Whether or not this device is capable of executing tasks by using {@link #execute(Runnable)}.
     */
    boolean supportsExecution();

    /**
     * @return Whether or not this device is capable of containing processes.
     */
    boolean supportsProcesses();
}
