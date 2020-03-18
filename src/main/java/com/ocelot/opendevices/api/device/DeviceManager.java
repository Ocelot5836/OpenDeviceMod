package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>Manages the locating and communication of devices.</p>
 *
 * @author Ocelot
 */
public class DeviceManager extends WorldSavedData
{
    public static final String NAME = OpenDevices.MOD_ID + "_device_manager";

    private Map<UUID, Pair<ResourceLocation, CompoundNBT>> devices;
    private ServerWorld world;

    public DeviceManager()
    {
        super(NAME);
        this.devices = new HashMap<>();
        this.world = null;
    }

    /**
     * Adds the specified device to the world. Must be called whenever a change is made to the location of the device.
     *
     * @param device     The device to add
     * @param serializer The serializer used to read/write location information to/from file
     * @param <T>        The type of device being added
     */
    public <T extends Device> void add(T device, DeviceSerializer<T> serializer)
    {
        if (!DeviceRegistries.DEVICE_SERIALIZERS.containsValue(serializer))
        {
            OpenDevices.LOGGER.warn("Could not add device with address '" + device.getAddress() + "' as serializer '" + serializer.getClass().getName() + "' is not registered. Skipping!");
            return;
        }
        this.devices.put(device.getAddress(), new ImmutablePair<>(DeviceRegistries.DEVICE_SERIALIZERS.getKey(serializer), serializer.write(this.world, device)));
        this.markDirty();
    }

    /**
     * Removes the specified device from the world.
     *
     * @param address The address of the device to remove
     */
    public void remove(UUID address)
    {
        this.devices.remove(address);
        this.markDirty();
    }

    /**
     * Checks for the address with the specified ID.
     *
     * @param address The address of the device to check
     * @return The device found or null if it doesn't exist or could not be accessed
     */
    @Nullable
    public Device locate(UUID address)
    {
        if (!this.exists(address))
        {
            OpenDevices.LOGGER.warn("Could not locate device with address '" + address + "' as it does not exist.");
            return null;
        }

        if (!DeviceRegistries.DEVICE_SERIALIZERS.containsKey(this.devices.get(address).getLeft()))
        {
            OpenDevices.LOGGER.warn("Could not read device with address '" + address + "' as serializer '" + this.devices.get(address) + "' is not registered. Skipping!");
            return null;
        }

        DeviceSerializer<? extends Device> serializer = Objects.requireNonNull(DeviceRegistries.DEVICE_SERIALIZERS.getValue(this.devices.get(address).getLeft()));
        CompoundNBT nbt = this.devices.get(address).getRight();
        return serializer.canRead(this.world, address, nbt) ? serializer.read(this.world, address, nbt) : null;
    }

    /**
     * Checks for the address with the specified ID.
     *
     * @param address The address of the device to check
     * @return Whether or not a device with the specified address exists
     */
    public boolean exists(UUID address)
    {
        return this.devices.containsKey(address);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.devices.clear();
        ListNBT devicesNbt = nbt.getList("devices", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < devicesNbt.size(); i++)
        {
            CompoundNBT deviceNbt = devicesNbt.getCompound(i);

            UUID address = deviceNbt.getUniqueId("address");
            ResourceLocation serializer = new ResourceLocation(deviceNbt.getString("serializer"));
            CompoundNBT data = deviceNbt.getCompound("data");
            if (!DeviceRegistries.DEVICE_SERIALIZERS.containsKey(serializer))
            {
                OpenDevices.LOGGER.warn("Could not read device with address '" + address + "' as serializer '" + serializer + "' is not registered. Skipping!");
                continue;
            }

            this.devices.put(address, new ImmutablePair<>(serializer, data));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT devicesNbt = new ListNBT();
        this.devices.forEach((address, pair) ->
        {
            CompoundNBT deviceNbt = new CompoundNBT();
            deviceNbt.putUniqueId("address", address);
            deviceNbt.putString("serializer", pair.getLeft().toString());
            deviceNbt.put("data", pair.getRight());
            devicesNbt.add(deviceNbt);
        });
        nbt.put("devices", devicesNbt);
        return nbt;
    }

    /**
     * Fetches an instance of the device manager from the server.
     *
     * @param world The world to fetch the data from
     * @return The device manager for that world
     */
    public static DeviceManager get(ServerWorld world)
    {
        DeviceManager deviceManager = world.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(DeviceManager::new, NAME);
        deviceManager.world = world;
        return deviceManager;
    }
}
