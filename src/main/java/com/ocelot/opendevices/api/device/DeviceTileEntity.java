package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A tile entity that defines a tile entity device.</p>
 *
 * @author Ocelot
 * @see LaptopTileEntity
 */
public abstract class DeviceTileEntity extends TileEntity
{
    public DeviceTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    /**
     * Randomizes the address of this device.
     */
    protected void randomizeAddress()
    {
    }

    /**
     * Notifies the world that an update has occurred. Should be called whenever something should be saved to file.
     */
    public void notifyUpdate()
    {
        this.markDirty();
        if (this.world != null)
        {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    /**
     * Use this to save any information that should be saved on destroy. Otherwise use {@link DeviceTileEntity#write(CompoundNBT)}.
     *
     * @param nbt The tag to write the data to
     */
    public abstract void save(CompoundNBT nbt);

    /**
     * Use this to load any information that should be read on place. Otherwise use {@link DeviceTileEntity#read(CompoundNBT)}.
     *
     * @param nbt The tag to read the data from
     */
    public abstract void load(CompoundNBT nbt);

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad()
    {
        if (this.world != null && !this.world.isRemote() && this instanceof Device)
        {
            Device device = (Device) this;
            DeviceManager deviceManager = DeviceManager.get(this.world);
            if (device.getAddress() != null && !deviceManager.exists(device.getAddress()))
                deviceManager.add(device, (DeviceSerializer<? super Device>) device.getSerializer());
        }
    }

    @Override
    public void remove()
    {
        super.remove();
        if (this.world != null && !this.world.isRemote() && this instanceof Device)
        {
            DeviceManager.get(this.world).remove(((Device) this).getAddress());
        }
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);

        if (nbt.contains("device", Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT data = nbt.getCompound("device");
            this.load(data);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);

        CompoundNBT data = new CompoundNBT();
        this.save(data);
        nbt.put("device", data);

        return nbt;
    }

    public IWorld getDeviceWorld()
    {
        return world;
    }

    public BlockPos getDevicePos()
    {
        return this.pos;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }
}
