package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializers;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * <p>Used by {@link DeviceSerializers#TILE_ENTITY} to determine where a device is.</p>
 *
 * @author Ocelot
 */
public interface TileEntityDevice extends Device
{
    /**
     * @return The key to this device dimension
     */
    default RegistryKey<World> getDeviceDimensionKey()
    {
        return this.getDeviceWorld().getDimensionKey();
    }

    /**
     * @return The position of this device
     */
    BlockPos getDevicePos();
}
