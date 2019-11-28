package com.ocelot.opendevices.api.component;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Serializes any {@link Component} that implements {@link INBTSerializable} to/from NBT.</p>
 *
 * @param <T> The type of component being serialized
 * @author Ocelot
 */
public class StandardComponentSerializer<T extends Component & INBTSerializable<CompoundNBT>> extends ForgeRegistryEntry<ComponentSerializer<?>> implements ComponentSerializer<T>
{
    private Function<CompoundNBT, T> factory;

    public StandardComponentSerializer(Supplier<T> factory)
    {
        this(nbt ->
        {
            T component = factory.get();
            component.deserializeNBT(nbt);
            return component;
        });
    }

    public StandardComponentSerializer(Function<CompoundNBT, T> factory)
    {
        this.factory = factory;
    }

    @Override
    public T deserializeNBT(CompoundNBT nbt)
    {
        return this.factory.apply(nbt);
    }
}
