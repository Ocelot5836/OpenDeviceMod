package io.github.ocelot.opendevices.api.device.serializer;

import io.github.ocelot.opendevices.api.device.Device;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * <p>Determines how to serialize a device position to/from NBT and buffer.</p>
 *
 * @param <T> The type of device to serialize
 * @author Ocelot
 */
public interface DeviceSerializer<T extends Device> extends IForgeRegistryEntry<DeviceSerializer<?>>
{
    /**
     * Writes the specified device location into NBT.
     *
     * @param device The device to write
     * @param nbt    The tag to write the device into
     */
    void serialize(World world, T device, CompoundNBT nbt);

    /**
     * Reads a device from NBT.
     *
     * @param nbt The tag to read the device from
     * @return The device at the read position or <code>null</code> if the device could not be found
     */
    @Nullable
    T deserialize(World world, CompoundNBT nbt);
}
