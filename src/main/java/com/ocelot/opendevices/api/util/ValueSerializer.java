package com.ocelot.opendevices.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * <p>Manages serialization of modified values instead of every single value.</p>
 *
 * @author Ocelot
 */
public interface ValueSerializer
{
    /**
     * Marks the specified value as changed.
     *
     * @param key The value to mark
     */
    void markDirty(String key);

    /**
     * Discards any changes that have been made.
     */
    void discardChanges();

    /**
     * Writes all values queued to be sent to the client  to NBT.
     *
     * @return The tag full of data
     */
    CompoundNBT writeClient();

    /**
     * Loads all values queued to be sent to the client from NBT.
     *
     * @param nbt The tag full of data
     */
    void readClient(CompoundNBT nbt);

    /**
     * Writes all values to NBT.
     *
     * @return The tag full of data
     */
    CompoundNBT write();

    /**
     * Loads all values from NBT.
     *
     * @param nbt The tag full of data
     */
    void read(CompoundNBT nbt);
}
