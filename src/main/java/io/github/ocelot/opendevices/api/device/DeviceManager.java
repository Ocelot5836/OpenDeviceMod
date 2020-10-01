package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.DeviceRegistries;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
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
     * Adds the specified device to the device manager.
     *
     * @param device The device to add
     * @param <T>    The type of device being added
     */
    @SuppressWarnings("unchecked")
    public <T extends Device> void addDevice(T device)
    {
        UUID address = device.getAddress();
        if (this.devices.containsKey(address))
        {
            LOGGER.warn("Failed to add device with address '" + address + "' as it has already been added.");
            return;
        }

        try
        {
            DeviceSerializer<T> serializer = (DeviceSerializer<T>) DeviceRegistries.DEVICE_SERIALIZERS.getValue(device.getSerializer().getRegistryName());
            if (serializer == null)
                throw new IllegalArgumentException("Unknown device serializer '" + device.getSerializer().getRegistryName() + "'");

            CompoundNBT data = new CompoundNBT();
            serializer.serialize(this.world, device, data);
            data.putString("Serializer", String.valueOf(device.getSerializer().getRegistryName()));
            this.devices.put(address, data);
            this.deviceCache.put(address, device);
            this.markDirty();
            // TODO notify listeners
        }
        catch (Exception e)
        {
            LOGGER.error("Error writing device location for '" + address + "' to NBT. Skipping!", e);
        }
    }

    /**
     * Removes the specified device if it exists.
     *
     * @param address The id of the device to remove
     */
    public void removeDevice(UUID address)
    {
        this.deviceCache.remove(address);
        if (this.devices.remove(address) != null)
        {
            this.markDirty();
            // TODO notify listeners
        }
    }

    /**
     * Checks to see if a device with the specified address is already added.
     *
     * @param address The address to check
     * @return Whether or not the address is already added
     */
    public boolean exists(UUID address)
    {
        return this.devices.containsKey(address);
    }

    /**
     * Fetches the device by the specified id.
     *
     * @param address The id of the device to get
     * @param <T>     The type of device requested
     * @return An optional with that device
     */
    @SuppressWarnings("unchecked")
    public <T extends Device> Optional<T> getDevice(UUID address)
    {
        if (this.deviceCache.containsKey(address))
            return (Optional<T>) Optional.of(this.deviceCache.get(address));
        if (!this.devices.containsKey(address))
            return Optional.empty();

        CompoundNBT data = this.devices.get(address);

        Optional<T> device = readDevice(this.world, address, data);
        if (!device.isPresent())
        {
            this.devices.remove(address);
            this.markDirty();
            return Optional.empty();
        }

        this.deviceCache.put(address, device.get());
        return device;
    }

    /**
     * Fetches the data for the specified address.
     *
     * @param address The address to get data for
     * @return The location data at that address
     */
    public Optional<CompoundNBT> getData(UUID address)
    {
        return Optional.ofNullable(this.devices.get(address));
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.deviceCache.clear();
        this.devices.clear();

        ListNBT devicesNbt = nbt.getList("Devices", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < devicesNbt.size(); i++)
        {
            CompoundNBT deviceNbt = devicesNbt.getCompound(i);
            this.devices.put(deviceNbt.getUniqueId("Address"), deviceNbt.getCompound("Data"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT devicesNbt = new ListNBT();
        this.devices.forEach((address, data) ->
        {
            CompoundNBT deviceNbt = new CompoundNBT();
            deviceNbt.putUniqueId("Address", address);
            deviceNbt.put("Data", data);
            devicesNbt.add(deviceNbt);
        });
        nbt.put("Devices", devicesNbt);

        return nbt;
    }

    /**
     * Reads a device from the specified world.
     *
     * @param world   The world to get the device from
     * @param address The address of the device
     * @param data    The location data associated with the device
     * @param <T>     The type of device requested
     * @return An optional with that device
     */
    @SuppressWarnings("unchecked")
    public static <T extends Device> Optional<T> readDevice(World world, UUID address, CompoundNBT data)
    {
        try
        {
            DeviceSerializer<?> serializer = DeviceRegistries.DEVICE_SERIALIZERS.getValue(ResourceLocation.tryCreate(data.getString("Serializer")));
            if (serializer == null)
                throw new IllegalArgumentException("Unknown device serializer '" + data.getString("Serializer") + "'");

            T device = (T) serializer.deserialize(world, data);
            if (device == null)
                throw new IllegalStateException("Could not locate device");

            return Optional.of(device);
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading device with address '" + address + "' from NBT. Skipping!", e);
        }

        return Optional.empty();
    }

    /**
     * Fetches the device manager for the specified world.
     *
     * @param world The world to get the manager from
     * @return The device manager for the world
     */
    public static DeviceManager get(IServerWorld world)
    {
        DeviceManager deviceManager = world.getWorld().getServer().func_241755_D_().getSavedData().getOrCreate(DeviceManager::new, NAME);
        deviceManager.world = world.getWorld();
        return deviceManager;
    }
}
