package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.api.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * <p>A process is a continuous task that is being executed by a {@link Device}.</p>
 *
 * @author Ocelot
 */
public interface DeviceProcess<T extends Device>
{
    /**
     * Updates this process.
     */
    void update(T device);

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
     * @return The id of this process
     */
    UUID getProcessId();

    /**
     * Writes any persistent data to file.
     *
     * @return The tag full of data
     */
    CompoundNBT save();

    /**
     * Writes any data that will be needed after calling {@link Laptop#syncProcess(UUID)}.
     *
     * @return The tag full of data
     */
    default CompoundNBT writeSyncNBT()
    {
        return new CompoundNBT();
    }

    /**
     * Reads any data from the specified tag on the other logical side after {@link Laptop#syncProcess(UUID)} is called.
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
