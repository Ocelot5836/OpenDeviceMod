package io.github.ocelot.opendevices.api.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.api.listener.ProcessListener;
import io.github.ocelot.opendevices.core.device.ProcessManagerImpl;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Manages processes for a specified type of device.
 *
 * @param <T> The type of device to get processes for
 */
public interface ProcessManager<T extends Device> extends INBTSerializable<CompoundNBT>
{
    /**
     * Creates a new standard process manager with 50 maximum processes.
     *
     * @param device The device to make a manager for
     * @param <T>    The type of device to make a manager for
     * @return A new process manager for the specified device
     */
    static <T extends Device> ProcessManager<T> create(T device)
    {
        return new ProcessManagerImpl<>(device, 50);
    }

    /**
     * Creates a new standard process manager.
     *
     * @param device       The device to make a manager for
     * @param maxProcesses The maximum amount of processes supported by the device
     * @param <T>          The type of device to make a manager for
     * @return A new process manager for the specified device
     */
    static <T extends Device> ProcessManager<T> create(T device, int maxProcesses)
    {
        return new ProcessManagerImpl<>(device, maxProcesses);
    }

    /**
     * Updates all child processes.
     */
    void tick();

    /**
     * Adds the specified listener for start/stop events.
     *
     * @param listener The listener to add
     */
    void addListener(ProcessListener listener);

    /**
     * Removes the specified listener from start/stop events.
     *
     * @param listener The listener to remove
     */
    void removeListener(ProcessListener listener);

    /**
     * Attempts to start a new process of the specified type.
     *
     * @param process The process to start
     * @return The id of the process created or <code>null</code> if the process creation failed
     */
    @Nullable
    UUID startProcess(DeviceProcess<T> process);

    /**
     * Attempts to terminate the specified process.
     *
     * @param processId The id of the process to stop
     * @return Whether or not the process was able to be stopped
     */
    boolean endProcess(UUID processId);

    /**
     * @return A stream of all processes
     */
    Stream<DeviceProcess<T>> getProcesses();
}
