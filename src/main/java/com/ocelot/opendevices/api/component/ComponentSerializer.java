package com.ocelot.opendevices.api.component;

import net.minecraft.nbt.CompoundNBT;

/**
 * <p>Used to serialize and deserialize any {@link Component} to/from NBT.</p>
 *
 * @author Ocelot
 */
public interface ComponentSerializer<T extends Component>
{
    /**
     * Serializes the specified component to NBT.
     *
     * @param component The component to serialize
     * @return The NBT data of the component
     */
    CompoundNBT serializeNBT(T component);

    /**
     * Deserializes the specified component from NBT.
     *
     * @param nbt The data to create the component from
     * @return The component from NBT
     */
    T deserializeNBT(CompoundNBT nbt);
}
