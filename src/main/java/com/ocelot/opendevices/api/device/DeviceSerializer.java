package com.ocelot.opendevices.api.device;

import net.minecraft.nbt.CompoundNBT;
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
    @Nullable
    T read(ServerWorld world, UUID address, CompoundNBT nbt);

    CompoundNBT write(ServerWorld world, T device);

    boolean canRead(ServerWorld world, UUID address, CompoundNBT nbt);

    /**
     * Registers a new type of device serializer for the {@link DeviceManager}.
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
