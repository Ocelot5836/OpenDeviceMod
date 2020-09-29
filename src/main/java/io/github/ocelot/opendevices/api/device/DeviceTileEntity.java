package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializers;
import io.github.ocelot.opendevices.api.device.serializer.TileEntityDevice;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

/**
 * <p>A simple implementation of {@link TileEntityDevice}.</p>
 *
 * @author Ocelot
 */
public class DeviceTileEntity extends TileEntity implements TileEntityDevice
{
    private UUID id;

    public DeviceTileEntity(TileEntityType<? extends DeviceTileEntity> tileEntityType)
    {
        super(tileEntityType);
        this.id = UUID.randomUUID();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.id = nbt.hasUniqueId("Id") ? nbt.getUniqueId("Id") : UUID.randomUUID();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        nbt.putUniqueId("Id", this.id);
        return nbt;
    }

    @Override
    public RegistryKey<World> getDeviceDimensionKey()
    {
        return Objects.requireNonNull(this.world).getDimensionKey();
    }

    @Override
    public BlockPos getDevicePos()
    {
        return this.pos;
    }

    @Override
    public DeviceSerializer<?> getSerializer()
    {
        return DeviceSerializers.TILE_ENTITY;
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    /**
     * Sets the id for this device.
     *
     * @param id The new device id
     */
    protected void setId(UUID id)
    {
        this.id = id;
    }
}
