package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializers;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

/**
 * <p>A simple implementation of {@link TileEntityDevice}.</p>
 *
 * @author Ocelot
 */
public abstract class DeviceTileEntity extends TileEntity implements TileEntityDevice
{
    private UUID address;

    public DeviceTileEntity(TileEntityType<? extends DeviceTileEntity> tileEntityType)
    {
        super(tileEntityType);
        this.address = UUID.randomUUID();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.address = nbt.hasUniqueId("Address") ? nbt.getUniqueId("Address") : UUID.randomUUID();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        nbt.putUniqueId("Address", this.address);
        return nbt;
    }

    /**
     * Writes all client relating data to the specified tag.
     *
     * @param nbt The tag to write to
     * @return The input tag
     */
    protected CompoundNBT writeClient(CompoundNBT nbt)
    {
        return this.write(nbt);
    }

    /**
     * Reads all client relating data from the specified tag.
     *
     * @param state The current block state of the device
     * @param nbt   The tag to read data from
     */
    protected void readClient(BlockState state, CompoundNBT nbt)
    {
        this.read(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.writeClient(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.readClient(this.getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.world != null && !this.world.isRemote())
        {
            DeviceManager deviceManager = DeviceManager.get((IServerWorld) this.world);
            if (!deviceManager.exists(this.getAddress()))
                deviceManager.addDevice(this);
        }
    }

    @Override
    public void remove()
    {
        super.remove();
        if (this.world != null && !this.world.isRemote())
            DeviceManager.get((IServerWorld) this.world).removeDevice(this.getAddress());
    }

    @Override
    public World getDeviceWorld()
    {
        return Objects.requireNonNull(this.world);
    }

    @Override
    public BlockPos getDevicePos()
    {
        return pos;
    }

    @Override
    public DeviceSerializer<?> getSerializer()
    {
        return DeviceSerializers.TILE_ENTITY;
    }

    @Override
    public UUID getAddress()
    {
        return address;
    }

    /**
     * Sets the id for this device.
     *
     * @param address The new device id
     */
    protected void setAddress(UUID address)
    {
        this.address = address;
    }
}
