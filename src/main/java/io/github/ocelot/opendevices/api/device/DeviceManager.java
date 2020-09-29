package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.DeviceRegistries;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class DeviceManager extends WorldSavedData
{
    private static final String NAME = OpenDevices.MOD_ID + "_DeviceManager";
    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<UUID, Device> deviceCache;
    private final Map<UUID, CompoundNBT> devices;

    private ServerWorld world;

    public DeviceManager()
    {
        super(NAME);
        this.deviceCache = new HashMap<>();
        this.devices = new HashMap<>();
    }

    /**
     * Removes the specified device if it exists.
     *
     * @param id The id of the device to remove
     */
    public void remove(UUID id)
    {
        this.deviceCache.remove(id);
        if (this.devices.remove(id) != null)
        {
            this.markDirty();
            // TODO notify listeners
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Device> void add(T device)
    {
        UUID id = device.getId();
        if (this.devices.containsKey(id))
        {
            LOGGER.warn("Failed to add device with id '" + id + "' as it has already been added.");
            return;
        }

        try
        {
            DeviceSerializer<T> serializer = (DeviceSerializer<T>) DeviceRegistries.DEVICE_SERIALIZERS.getValue(device.getSerializer().getRegistryName());
            if (serializer == null)
                throw new IllegalArgumentException("Unknown device serializer '" + device.getSerializer().getRegistryName() + "'");

            CompoundNBT data = new CompoundNBT();
            serializer.serialize(this.world, device, data);
            this.devices.put(id, data);
            this.deviceCache.put(id, device);
        }
        catch (Exception e)
        {
            LOGGER.error("Error writing device location for '" + id + "' to NBT. Skipping!", e);
        }
    }

    /**
     * Fetches the device by the specified id.
     *
     * @param id  The id of the device to get
     * @param <T> The type of device requested
     * @return An optional with that device
     */
    @SuppressWarnings("unchecked")
    public <T extends Device> Optional<T> get(UUID id)
    {
        if (this.deviceCache.containsKey(id))
            return (Optional<T>) Optional.of(this.deviceCache.get(id));
        if (!this.devices.containsKey(id))
            return Optional.empty();

        CompoundNBT nbt = this.devices.get(id);
        try
        {
            DeviceSerializer<?> serializer = DeviceRegistries.DEVICE_SERIALIZERS.getValue(new ResourceLocation(nbt.getString("Serializer")));
            if (serializer == null)
                throw new IllegalArgumentException("Unknown device serializer '" + nbt.getString("Serializer") + "'");

            T device = (T) serializer.deserialize(this.world, nbt);
            if (device == null)
                throw new IllegalStateException("Could not locate device");

            this.deviceCache.put(id, device);
            return Optional.of(device);
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading device location for '" + id + "' from NBT. Skipping!", e);
            this.devices.remove(id);
        }

        return Optional.empty();
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.deviceCache.clear();
        this.devices.clear();

        ListNBT devicesNbt = nbt.getList("Devices", Constants.NBT.TAG_LIST);
        for (int i = 0; i < devicesNbt.size(); i++)
        {
            CompoundNBT deviceNbt = devicesNbt.getCompound(i);
            this.devices.put(deviceNbt.getUniqueId("Id"), deviceNbt.getCompound("Data"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT devicesNbt = new ListNBT();
        this.devices.forEach((id, data) ->
        {
            CompoundNBT deviceNbt = new CompoundNBT();
            deviceNbt.putUniqueId("Id", id);
            deviceNbt.put("Data", data);
        });
        nbt.put("Devices", devicesNbt);

        return nbt;
    }

    /**
     * Fetches the device manager for the specified world.
     *
     * @param world The world to get the manager from
     * @return The device manager for the world
     */
    public static DeviceManager get(IServerWorld world)
    {
        DeviceManager deviceManager = world.getWorld().getSavedData().getOrCreate(DeviceManager::new, NAME);
        deviceManager.world = world.getWorld();
        return deviceManager;
    }
}
