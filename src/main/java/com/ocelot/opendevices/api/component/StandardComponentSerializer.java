package com.ocelot.opendevices.api.component;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

/**
 * <p>Serializes any {@link Component} that implements {@link INBTSerializable} to/from NBT.</p>
 *
 * @param <T> The type of component being serialized
 * @author Ocelot
 */
public class StandardComponentSerializer<T extends Component & INBTSerializable<CompoundNBT>> extends ForgeRegistryEntry<ComponentSerializer<?>> implements ComponentSerializer<T>
{
    private Supplier<T> factory;
    private Class<T> componentClass;

    public StandardComponentSerializer(Class<T> componentClass, Supplier<T> factory)
    {
        this.componentClass = componentClass;
        this.factory = factory;
    }

    @Override
    public T deserializeNBT(CompoundNBT nbt)
    {
        T component = this.factory.get();
        component.deserializeNBT(nbt);
        return component;
    }

    @Override
    public Class<T> getComponentClass()
    {
        return componentClass;
    }
}
