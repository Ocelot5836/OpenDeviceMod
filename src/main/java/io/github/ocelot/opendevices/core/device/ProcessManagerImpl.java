package io.github.ocelot.opendevices.core.device;

import io.github.ocelot.opendevices.api.DeviceRegistries;
import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.api.device.process.DeviceProcess;
import io.github.ocelot.opendevices.api.device.process.DeviceProcessSerializer;
import io.github.ocelot.opendevices.api.device.process.ProcessManager;
import io.github.ocelot.opendevices.api.listener.ProcessListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Stream;

public class ProcessManagerImpl<T extends Device> implements ProcessManager<T>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final T device;
    private final int maxProcesses;
    private final Map<UUID, DeviceProcess<T>> processes;
    private final Set<ProcessListener> listeners;

    public ProcessManagerImpl(T device, int maxProcesses)
    {
        this.device = device;
        this.maxProcesses = maxProcesses;
        this.processes = new HashMap<>();
        this.listeners = new HashSet<>();
    }

    @Override
    public void tick()
    {
        this.processes.values().forEach(DeviceProcess::tick);
    }

    @Override
    public void addListener(ProcessListener listener)
    {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ProcessListener listener)
    {
        this.listeners.remove(listener);
    }

    @Override
    public UUID startProcess(DeviceProcess<T> process)
    {
        if(this.processes.size() >= this.maxProcesses)
            return null;

        UUID processId = UUID.randomUUID(); // TODO start processes
        this.processes.put(processId, process);
        this.listeners.forEach(listener -> listener.onProcessStart(processId));
        return processId;
    }

    @Override
    public boolean endProcess(UUID processId)
    {
        this.processes.remove(processId);
        this.listeners.forEach(listener -> listener.onProcessStop(processId));
        return false;
    }

    @Override
    public Stream<DeviceProcess<T>> getProcesses()
    {
        return this.processes.values().stream();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT processesNbt = new ListNBT();
        this.processes.forEach((processId, process) ->
        {
            CompoundNBT processNbt = new CompoundNBT();
            processNbt.putString("Serializer", String.valueOf(process.getProcessType().getRegistryName()));
            processNbt.putUniqueId("Id", processId);

            CompoundNBT processData = new CompoundNBT();
            process.write(processData);
            processNbt.put("Data", processData);

            processesNbt.add(processNbt);
        });
        nbt.put("Processes", processesNbt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.processes.clear();

        ListNBT processesNbt = nbt.getList("Processes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < processesNbt.size(); i++)
        {
            CompoundNBT processNbt = processesNbt.getCompound(i);
            ResourceLocation serializer = ResourceLocation.tryCreate(processNbt.getString("Serializer"));
            UUID processId = processNbt.getUniqueId("Id");

            if (serializer == null)
            {
                LOGGER.warn("Invalid process serializer '" + processNbt.getString("Serializer") + "'");
                continue;
            }

            Optional<DeviceProcess<T>> process = readProcess(this.device, processId, serializer, processNbt.getCompound("Data"));
            if (!process.isPresent())
                continue;

            this.processes.put(processId, process.get());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Device> Optional<DeviceProcess<T>> readProcess(T device, UUID processId, ResourceLocation serializerId, CompoundNBT data)
    {
        try
        {
            DeviceProcessSerializer<T> serializer = (DeviceProcessSerializer<T>) DeviceRegistries.DEVICE_PROCESSES.getValue(serializerId);
            if (serializer == null)
                throw new IllegalArgumentException("Unknown process serializer '" + serializerId + "'");

            DeviceProcess<T> process = serializer.create(device, processId);
            if (process == null)
                throw new IllegalStateException("Could not create process");

            process.read(data);
            return Optional.of(process);
        }
        catch (Exception e)
        {
            LOGGER.error("Error creating process with id '" + processId + "' from NBT. Skipping!", e);
        }

        return Optional.empty();
    }
}
