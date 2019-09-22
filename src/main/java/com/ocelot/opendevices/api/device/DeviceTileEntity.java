package com.ocelot.opendevices.api.device;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * <p>A tile entity that defines a device. Make sure to extend this class if you want to make your own device.</p>
 *
 * @author Ocelot
 * @see com.ocelot.opendevices.tileentity.LaptopTileEntity
 */
public abstract class DeviceTileEntity extends TileEntity
{
    public DeviceTileEntity(TileEntityType<?> type)
    {
        super(type);
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
