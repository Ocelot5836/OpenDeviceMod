package com.ocelot.opendevices.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * <p>Manages serialization of modified values instead of every single value.</p>
 *
 * @author Ocelot
 */
public interface ClientSerializer extends INBTSerializable<CompoundNBT>
{
    /**
     * Marks the specified value as changed.
     *
     * @param key The value to mark
     */
    void markDirty(String key);
}
