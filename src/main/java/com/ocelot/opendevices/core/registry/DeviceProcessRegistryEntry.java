package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class DeviceProcessRegistryEntry extends ForgeRegistryEntry<DeviceProcessRegistryEntry>
{
    private Class<? extends DeviceProcess<?>> clazz;

    public DeviceProcessRegistryEntry(Class<? extends DeviceProcess<?>> clazz)
    {
        this.clazz = clazz;
    }

    @Nullable
    public <T extends Device> DeviceProcess<T> createProcess(UUID processId)
    {
        try
        {
            return (DeviceProcess<T>) this.clazz.getConstructor(UUID.class).newInstance(processId);
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create process: " + this.getRegistryName() + ". Verify there is a public constructor with a UUID and a CompoundNBT as an argument.", e);
        }

        return null;
    }

    @Nullable
    public <T extends Device> DeviceProcess<T> createProcess(UUID processId, CompoundNBT nbt)
    {
        try
        {
            return (DeviceProcess<T>) this.clazz.getConstructor(UUID.class, CompoundNBT.class).newInstance(processId, nbt);
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create process: " + this.getRegistryName() + ". Verify there is a public constructor with a UUID as an argument.", e);
        }

        return null;
    }

    public Class<? extends DeviceProcess<?>> getTaskClass()
    {
        return clazz;
    }

}