package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
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
    private Set<String> markedFields;
    private Set<String> modifiedFields;
    private Map<String, Pair<Consumer<CompoundNBT>, Consumer<CompoundNBT>>> serializers;

    public SyncHelper(@Nullable Runnable markDirty)
    {
        this.markDirty = markDirty;
        this.markedFields = new HashSet<>();
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

        this.markedFields.add(key);
        this.modifiedFields.add(key);
        if (this.markDirty != null)
            this.markDirty.run();
    }

    @Override
    public void discardChanges()
    {
        this.markedFields.clear();
    }

    @Override
    public CompoundNBT writeClient()
    {
        CompoundNBT nbt = new CompoundNBT();
        this.markedFields.forEach(fieldName ->
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
    public void readClient(CompoundNBT nbt)
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
    public CompoundNBT write()
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT modifiedFieldsNbt = new ListNBT();
        this.modifiedFields.forEach(fieldName ->
        {
            if (!this.serializers.containsKey(fieldName))
            {
                OpenDevices.LOGGER.warn("Could not serialize field '" + fieldName + "' as it does not have a serializer.");
                return;
            }
            CompoundNBT fieldNbt = new CompoundNBT();
            fieldNbt.putString("fieldName", fieldName);

            CompoundNBT fieldDataNbt = new CompoundNBT();
            this.serializers.get(fieldName).getLeft().accept(fieldDataNbt);
            fieldNbt.put("data", fieldDataNbt);

            modifiedFieldsNbt.add(fieldNbt);
        });
        nbt.put("modifiedFields", modifiedFieldsNbt);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        ListNBT modifiedFieldsNbt = nbt.getList("modifiedFields", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < modifiedFieldsNbt.size(); i++)
        {
            CompoundNBT fieldNbt = modifiedFieldsNbt.getCompound(i);
            String fieldName = fieldNbt.getString("fieldName");
            CompoundNBT data = fieldNbt.getCompound("data");
            if (!this.serializers.containsKey(fieldName))
            {
                OpenDevices.LOGGER.warn("Could not deserialize field '" + fieldName + "' as it does not have a serializer.");
                return;
            }
            this.modifiedFields.add(fieldName);
            this.serializers.get(fieldName).getRight().accept(data);
        }
    }
}
