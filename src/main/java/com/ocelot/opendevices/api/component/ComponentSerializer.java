package com.ocelot.opendevices.api.component;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * <p>Used to serialize and deserialize any {@link Component} to/from NBT.</p>
 *
 * @param <T> The type of component being serialized
 * @author Ocelot
 */
public interface ComponentSerializer<T extends Component> extends IForgeRegistryEntry<ComponentSerializer<?>>
{
    /**
     * Deserializes the specified component from NBT.
     *
     * @param nbt The data to create the component from
     * @return The component from NBT
     */
    T deserializeNBT(CompoundNBT nbt);

    /**
     * @return The class of the component that gets serialized
     */
    Class<T> getComponentClass();

    /**
     * Registers a new component serializer that can be used to create a new {@link Component}.
     *
     * @author Ocelot
     * @see Component
     * @see ComponentSerializer
     */
    @interface Register
    {
        /**
         * @return The name of this serializer. Should be in the format of <code>modid:componentName</code>
         */
        String value();
    }
}
