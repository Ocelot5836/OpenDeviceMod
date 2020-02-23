package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.laptop.Computer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * <p>A process is a continuous task that is being executed by a {@link Device}.</p>
 *
 * @author Ocelot
 */
public interface DeviceProcess<T extends Device> extends INBTSerializable<CompoundNBT>
{
    /**
     * <p>Syncs this process's data with all clients using {@link #writeSyncNBT()} to write data for the client to read and {@link #readSyncNBT(CompoundNBT)} to read that data on the client side.</p>
     * <p>Equivalent to <code>this.getDevice().syncProcess(this.getProcessId());</code></p>
     */
    default void synchronizeClients()
    {
        this.getDevice().syncProcess(this.getProcessId());
    }

    /**
     * Initializes this process for the specified device once it has started execution.
     */
    void init();

    /**
     * Updates this process for the specified device.
     */
    void update();

    /**
     * Called when this process is terminated by the device.
     *
     * @param forced Whether or not this process self-terminated
     */
    default void onTerminate(boolean forced) {}

    /**
     * @return Whether or not this process has completed execution
     */
    boolean isTerminated();

    /**
     * @return The device this process is running on
     */
    T getDevice();

    /**
     * @return The id of this process
     */
    UUID getProcessId();

    /**
     * Writes any data that will be needed after calling {@link Computer#syncProcess(UUID)}.
     *
     * @return The tag full of data
     */
    default CompoundNBT writeSyncNBT()
    {
        return new CompoundNBT();
    }

    /**
     * Reads any data from the specified tag on the other logical side after {@link Computer#syncProcess(UUID)} is called.
     *
     * @param nbt The tag to read from
     */
    default void readSyncNBT(CompoundNBT nbt)
    {
    }

    /**
     * Registers a new type of process for a device.
     *
     * @author Ocelot
     */
    @Target(ElementType.TYPE)
    @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>.
         */
        String value();
    }
}
