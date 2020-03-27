package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.util.ValueSerializer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;

/**
 * <p>Manages common component aspects for {@link Component}.</p>
 *
 * @author Ocelot
 * @see Component
 */
public abstract class StandardComponent extends AbstractGui implements Component
{
    private ValueSerializer valueSerializer;
    private boolean dirty;

    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * @return The current client serializer of null if it has not been set
     */
    public ValueSerializer getValueSerializer()
    {
        return valueSerializer;
    }

    @Override
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    /**
     * Sets the client serializer to the provided value.
     *
     * @param valueSerializer The client serializer
     */
    protected void setValueSerializer(ValueSerializer valueSerializer)
    {
        this.valueSerializer = valueSerializer;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        return this.valueSerializer != null ? this.valueSerializer.serializeNBT() : new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (this.valueSerializer != null)
        {
            this.valueSerializer.deserializeNBT(nbt);
        }
    }
}
