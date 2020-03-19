package com.ocelot.opendevices.core.devicemanager;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SyncDevicesTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

public class DeviceManagerSavedData extends WorldSavedData implements DeviceManager
{
    public static final String NAME = OpenDevices.MOD_ID + "_device_manager";

    private Map<UUID, Pair<ResourceLocation, CompoundNBT>> devices;
    private ServerWorld world;
    private boolean syncing;

    public DeviceManagerSavedData()
    {
        super(NAME);
        this.devices = new HashMap<>();
        this.world = null;
        this.syncing = false;
    }

    // TODO add verification to see if the device still exists
    @Override
    public <T extends Device> void add(T device, DeviceSerializer<T> serializer)
    {
        if (!DeviceRegistries.DEVICE_SERIALIZERS.containsValue(serializer))
        {
            OpenDevices.LOGGER.warn("Could not add device with address '" + device.getAddress() + "' as serializer '" + serializer.getClass().getName() + "' is not registered. Skipping!");
            return;
        }
        CompoundNBT nbt = serializer.write(this.world, device);
        if (!serializer.exists(this.world, device.getAddress(), nbt))
        {
            OpenDevices.LOGGER.warn("Could not add device with address '" + device.getAddress() + "' as it does not exist in the world. Skipping!");
            return;
        }
        this.devices.put(device.getAddress(), new ImmutablePair<>(DeviceRegistries.DEVICE_SERIALIZERS.getKey(serializer), nbt));
        this.markDirty();
        TaskManager.sendToAll(new SyncDevicesTask(this.saveDevices()));
    }

    @Override
    public void remove(UUID address)
    {
        this.devices.remove(address);
        this.markDirty();
        TaskManager.sendToAll(new SyncDevicesTask(this.saveDevices()));
    }

    @Nullable
    @Override
    public Device locate(UUID address)
    {
        if (!this.exists(address))
        {
            OpenDevices.LOGGER.warn("Could not locate device with address '" + address + "' as it does not exist.");
            return null;
        }

        return parseDevice(this.world, address, this.devices.get(address).getLeft(), this.devices.get(address).getRight());
    }

    @Override
    public boolean exists(UUID address)
    {
        if (!this.devices.containsKey(address) || !DeviceRegistries.DEVICE_SERIALIZERS.containsKey(this.devices.get(address).getLeft()))
            return false;
        return Objects.requireNonNull(DeviceRegistries.DEVICE_SERIALIZERS.getValue(this.devices.get(address).getLeft())).exists(this.world, address, this.devices.get(address).getRight());
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        loadDevices(this.devices, nbt.getList("devices", Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("devices", this.saveDevices());
        return nbt;
    }

    public ListNBT saveDevices()
    {
        return saveDevices(this.devices);
    }

    public void setWorld(ServerWorld world)
    {
        this.world = world;
    }

    public static ListNBT saveDevices(Map<UUID, Pair<ResourceLocation, CompoundNBT>> devices)
    {
        ListNBT devicesNbt = new ListNBT();
        devices.forEach((address, pair) ->
        {
            CompoundNBT deviceNbt = new CompoundNBT();
            deviceNbt.putUniqueId("address", address);
            deviceNbt.putString("serializer", pair.getLeft().toString());
            deviceNbt.put("data", pair.getRight());
            devicesNbt.add(deviceNbt);
        });
        return devicesNbt;
    }

    public static void loadDevices(Map<UUID, Pair<ResourceLocation, CompoundNBT>> devices, ListNBT nbt)
    {
        devices.clear();
        for (int i = 0; i < nbt.size(); i++)
        {
            CompoundNBT deviceNbt = nbt.getCompound(i);

            UUID address = deviceNbt.getUniqueId("address");
            ResourceLocation serializer = new ResourceLocation(deviceNbt.getString("serializer"));
            CompoundNBT data = deviceNbt.getCompound("data");
            if (!DeviceRegistries.DEVICE_SERIALIZERS.containsKey(serializer))
            {
                OpenDevices.LOGGER.warn("Could not read device with address '" + address + "' as serializer '" + serializer + "' is not registered. Skipping!");
                continue;
            }

            devices.put(address, new ImmutablePair<>(serializer, data));
        }
    }

    public static Device parseDevice(World world, UUID address, ResourceLocation serializerName, CompoundNBT data)
    {
        if (!DeviceRegistries.DEVICE_SERIALIZERS.containsKey(serializerName))
        {
            OpenDevices.LOGGER.warn("Could not parse device with address '" + address + "' as serializer '" + serializerName + "' is not registered. Skipping!");
            return null;
        }

        DeviceSerializer<? extends Device> serializer = Objects.requireNonNull(DeviceRegistries.DEVICE_SERIALIZERS.getValue(serializerName));
        return serializer.canRead(world, address, data) ? serializer.read(world, address, data) : null;
    }
}
