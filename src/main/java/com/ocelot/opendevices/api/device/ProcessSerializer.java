package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the serialization and deserialization of {@link DeviceProcess}.
 *
 * @author Ocelot
 */
public class ProcessSerializer
{
    private static final Map<Class<? extends DeviceProcess<?>>, ResourceLocation> REGISTRY_CACHE = new HashMap<>();

    /**
     * Writes the specified process to NBT.
     *
     * @param process The process to write
     * @return The compound full of data
     */
    @Nullable
    public static CompoundNBT write(DeviceProcess<?> process)
    {
        try
        {
            ResourceLocation registryName = ProcessSerializer.getRegistryName(process);

            if (registryName == null)
            {
                OpenDevices.LOGGER.warn("Could not save process with class '" + process.getClass() + "' as it does not exist. Skipping!");
            }
            else
            {
                CompoundNBT processNbt = new CompoundNBT();
                processNbt.putString("processName", registryName.toString());
                processNbt.putUniqueId("processId", process.getProcessId());
                processNbt.put("data", process.serializeNBT());
                return processNbt;
            }
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not write process '" + getRegistryName(process) + "' to NBT!", e);
        }
        return null;
    }

    /**
     * Reads the specified process from NBT.
     *
     * @param deviceClass The class of the device reading the process
     * @param device      The device reading the process
     * @param nbt         The compound full of data
     * @param <T>         The type of device the process is being read for
     * @return The process generated from that data
     */
    @Nullable
    public static <T extends Device> DeviceProcess<T> read(Class<T> deviceClass, T device, CompoundNBT nbt)
    {
        ResourceLocation processName = new ResourceLocation(nbt.getString("processName"));
        DeviceProcessRegistryEntry entry = DeviceRegistries.PROCESSES.getValue(processName);

        if (entry == null)
        {
            OpenDevices.LOGGER.warn("Could not read process with name '" + processName + "' as it does not exist. Skipping!");
            return null;
        }

        UUID processId = nbt.getUniqueId("processId");

        try
        {
            return entry.createProcess(deviceClass, device, processId);
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not read process '" + processName + "' from NBT!", e);
        }
        return null;
    }

    /**
     * Checks the registry for a registry name under the specified process.
     *
     * @param process The process to get the registry name of
     * @param <T>     The type of device the process is being read for
     * @return The registry name of that task or null if the process is not registered
     */
    @Nullable
    public static <T extends Device> ResourceLocation getRegistryName(DeviceProcess<?> process)
    {
        if (DeviceRegistries.PROCESSES.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
        {
            for (Map.Entry<ResourceLocation, DeviceProcessRegistryEntry> entry : DeviceRegistries.PROCESSES.getEntries())
            {
                REGISTRY_CACHE.put(entry.getValue().getTaskClass(), entry.getKey());
            }
        }
        return REGISTRY_CACHE.get(process.getClass());
    }
}
