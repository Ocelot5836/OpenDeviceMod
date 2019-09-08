package com.ocelot.opendevices.tileentity;

import com.ocelot.opendevices.api.device.Device;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ModTileEntity extends TileEntity
{
    public ModTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    public void notifyUpdate()
    {
        this.markDirty();
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        if (this instanceof Device && nbt.contains("data", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = nbt.getCompound("data");
            ((Device) this).load(data);
        }
        super.read(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        if (this instanceof Device) {
            CompoundNBT data = new CompoundNBT();
            ((Device) this).save(data);
            nbt.put("data", data);
        }
        return nbt;
    }

    protected void readClient(CompoundNBT nbt)
    {
        this.read(nbt);
    }

    protected CompoundNBT writeClient(CompoundNBT nbt)
    {
        return this.write(nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.readClient(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.writeClient(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }
}
