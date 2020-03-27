package com.ocelot.opendevices.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * <p>Manages serialization of modified values instead of every single value.</p>
 *
 * @author Ocelot
 */
public interface ValueSerializer extends INBTSerializable<CompoundNBT>
{
    /**
     * Marks the specified value as changed.
     *
     * @param key The value to mark
     */
    void markDirty(String key);

    /**
     * Writes all values to NBT.
     *
     * @return The tag full of data
     */
    CompoundNBT save();

    /**
     * Loads all values from NBT.
     *
     * @param nbt The tag full of data
     */
    void load(CompoundNBT nbt);
}
