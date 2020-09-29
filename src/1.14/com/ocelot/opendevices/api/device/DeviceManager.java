package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.core.devicemanager.ClientDeviceManager;
import com.ocelot.opendevices.core.devicemanager.DeviceManagerSavedData;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Manages the locating and communication of devices.</p>
 *
 * @author Ocelot
 */
public interface DeviceManager
{
    /**
     * Adds the specified device to the world. Must be called whenever a change is made to the location of the device.
     *
     * @param device     The device to add
     * @param serializer The serializer used to read/write location information to/from file
     * @param <T>        The type of device being added
     * @throws UnsupportedOperationException If executed client side
     */
    <T extends Device> void add(T device, DeviceSerializer<T> serializer);

    /**
     * Removes the specified device from the world.
     *
     * @param address The address of the device to remove
     * @throws UnsupportedOperationException If executed client side
     */
    void remove(UUID address);

    /**
     * Checks for the address with the specified ID.
     *
     * @param address The address of the device to check
     * @return The device found or null if it doesn't exist or could not be accessed
     */
    @Nullable
    Device locate(UUID address);

    /**
     * Checks for the address with the specified ID.
     *
     * @param address The address of the device to check
     * @return Whether or not a device with the specified address exists
     */
    boolean exists(UUID address);

    /**
     * Fetches an instance of the device manager from the server.
     *
     * @param world The world to fetch the data from
     * @return The device manager for that world
     */
    static DeviceManager get(World world)
    {
        if (world.isRemote())
            return ClientDeviceManager.INSTANCE;
        if (!(world instanceof ServerWorld))
            throw new IllegalStateException("Server side world is not an instance of ServerWorld?");
        DeviceManagerSavedData deviceManager = (((ServerWorld) world).getSavedData()).getOrCreate(DeviceManagerSavedData::new, DeviceManagerSavedData.NAME);
        deviceManager.setWorld((ServerWorld) world);
        return deviceManager;
    }
}
