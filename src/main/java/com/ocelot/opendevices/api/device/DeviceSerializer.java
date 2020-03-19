package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.core.devicemanager.DeviceManagerSavedData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

/**
 * <p>Reads and writes a device location to and from NBT.</p>
 *
 * @param <T> The device this serializer works for
 * @author Ocelot
 */
public interface DeviceSerializer<T extends Device> extends IForgeRegistryEntry<DeviceSerializer<?>>
{
    /**
     * Locates a device from the provided NBT.
     *
     * @param world   The world being operated in
     * @param address The address of the device
     * @param nbt     The tag containing the position data
     * @return The device found or null if the device is not there
     */
    @Nullable
    T read(World world, UUID address, CompoundNBT nbt);

    /**
     * Saves the position of the specified device to NBT.
     *
     * @param world  The world being operated in
     * @param device The device to get the position info from
     * @return The tag containing the position data
     */
    CompoundNBT write(ServerWorld world, T device);

    /**
     * Checks to see if a device exists in the world from the provided NBT.
     *
     * @param world   The world being operated in
     * @param address The address of the device
     * @param nbt     The tag containing the position data
     * @return Whether or not the device can be found in the world
     */
    boolean exists(World world, UUID address, CompoundNBT nbt);

    /**
     * Checks if a device can be located from the provided NBT.
     *
     * @param world   The world being operated in
     * @param address The address of the device
     * @param nbt     The tag containing the position data
     * @return Whether or not the device is there
     */
    default boolean canRead(World world, UUID address, CompoundNBT nbt)
    {
        return this.exists(world, address, nbt);
    }

    /**
     * Registers a new type of device serializer for the {@link DeviceManagerSavedData}.
     *
     * @author Ocelot
     */
    @Target(ElementType.FIELD)
    @interface Register
    {
        /**
         * @return The name of this content. Should be in the format of <code>modid:contentName</code>.
         */
        String value();
    }
}
