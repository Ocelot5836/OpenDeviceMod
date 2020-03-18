package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.util.ClientSerializer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

/**
 * <p>Manages common component aspects for {@link Component}.</p>
 *
 * @author Ocelot
 * @see Component
 */
public abstract class StandardComponent extends AbstractGui implements Component
{
    private ClientSerializer clientSerializer;
    private boolean dirty;

    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * @return The current client serializer of null if it has not been set
     */
    public ClientSerializer getClientSerializer()
    {
        return clientSerializer;
    }

    @Override
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    /**
     * Sets the client serializer to the provided value.
     *
     * @param clientSerializer The client serializer
     */
    protected void setClientSerializer(ClientSerializer clientSerializer)
    {
        this.clientSerializer = clientSerializer;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        return this.clientSerializer != null ? this.clientSerializer.serializeNBT() : new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (this.clientSerializer != null)
        {
            this.clientSerializer.deserializeNBT(nbt);
        }
    }
}
