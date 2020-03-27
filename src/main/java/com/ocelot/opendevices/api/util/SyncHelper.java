package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.nbt.CompoundNBT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SyncHelper implements ValueSerializer
{
    private Runnable markDirty;
    private Set<String> modifiedFields;
    private Map<String, Pair<Consumer<CompoundNBT>, Consumer<CompoundNBT>>> serializers;

    public SyncHelper(@Nullable Runnable markDirty)
    {
        this.markDirty = markDirty;
        this.modifiedFields = new HashSet<>();
        this.serializers = new HashMap<>();
    }

    public void addSerializer(String fieldName, Consumer<CompoundNBT> serializer, Consumer<CompoundNBT> deserializer)
    {
        if (this.serializers.containsKey(fieldName))
        {
            throw new RuntimeException("Attempted to override existing field '" + fieldName + "'");
        }
        this.serializers.put(fieldName, new ImmutablePair<>(serializer, deserializer));
    }

    @Override
    public void markDirty(String key)
    {
        if (!this.serializers.containsKey(key))
        {
            OpenDevices.LOGGER.warn("Could not mark field '" + key + "' as dirty since it does not have a serializer.");
            return;
        }

        this.modifiedFields.add(key);
        if (this.markDirty != null)
            this.markDirty.run();
    }

    @Override
    public CompoundNBT save()
    {
        CompoundNBT nbt = new CompoundNBT();
        this.serializers.forEach((fieldName, serializer) -> serializer.getLeft().accept(nbt));
        return nbt;
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        nbt.keySet().forEach(fieldName ->
        {
            if (!this.serializers.containsKey(fieldName))
            {
                OpenDevices.LOGGER.warn("Could not deserialize field '" + fieldName + "' as it does not have a serializer.");
                return;
            }
            this.serializers.get(fieldName).getRight().accept(nbt);
        });
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        this.modifiedFields.forEach(fieldName ->
        {
            if (!this.serializers.containsKey(fieldName))
            {
                OpenDevices.LOGGER.warn("Could not serialize field '" + fieldName + "' as it does not have a serializer.");
                return;
            }
            this.serializers.get(fieldName).getLeft().accept(nbt);
        });
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.modifiedFields.clear();
        nbt.keySet().forEach(fieldName ->
        {
            if (!this.serializers.containsKey(fieldName))
            {
                OpenDevices.LOGGER.warn("Could not deserialize field '" + fieldName + "' as it does not have a serializer.");
                return;
            }
            this.serializers.get(fieldName).getRight().accept(nbt);
        });
    }
}
