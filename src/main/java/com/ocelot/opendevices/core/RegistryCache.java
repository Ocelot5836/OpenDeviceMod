package com.ocelot.opendevices.core;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryCache<T, V>
{
    private Supplier<Set<Map.Entry<ResourceLocation, T>>> registerGetter;
    private Function<T, V> converter;
    private Map<V, ResourceLocation> cache;

    public RegistryCache(Supplier<Set<Map.Entry<ResourceLocation, T>>> registerGetter, Function<T, V> converter)
    {
        this.registerGetter = registerGetter;
        this.converter = converter;
        this.cache = new HashMap<>();
    }

    @Nullable
    public ResourceLocation getRegistryName(V value)
    {
        Set<Map.Entry<ResourceLocation, T>> registry = this.registerGetter.get();
        if (registry.isEmpty())
            return null;

        if (this.cache.isEmpty())
        {
            for (Map.Entry<ResourceLocation, T> entry : registry)
            {
                this.cache.put(this.converter.apply(entry.getValue()), entry.getKey());
            }
        }
        return this.cache.get(value);
    }
}
