package com.ocelot.opendevices.api.device.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.DeviceRegistries;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>Manages the serialization and deserialization of {@link DeviceProcess} to and from NBT</p>.
 *
 * @author Ocelot
 */
public class ProcessSerializer
{
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
            ResourceLocation registryName = DeviceRegistries.getProcessRegistryName(process);

            if (registryName == null)
            {
                OpenDevices.LOGGER.warn("Could not save process with class '" + process.getClass() + "' as it is not registered. Skipping!");
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
            OpenDevices.LOGGER.error("Could not write process '" + DeviceRegistries.getProcessRegistryName(process) + "' to NBT!", e);
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
            OpenDevices.LOGGER.warn("Could not read process with name '" + processName + "' as it is not registered. Skipping!");
            return null;
        }

        try
        {
            DeviceProcess<T> process = entry.createProcess(deviceClass, device, nbt.getUniqueId("processId"));
            if (process == null)
                throw new IllegalArgumentException("Error creating new process.");
            process.deserializeNBT(nbt.getCompound("data"));
            return process;
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not read process '" + processName + "' from NBT!", e);
        }
        return null;
    }
}
