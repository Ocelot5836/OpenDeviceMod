package com.ocelot.opendevices.tileentity;

import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.nbt.CompoundNBT;

public class LaptopTileEntity extends ModTileEntity implements Device
{
    private byte test;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.TE_LAPTOP);
        this.test = 0;
    }

    @Override
    public void save(CompoundNBT nbt)
    {
        nbt.putByte("test", this.test);
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.test = nbt.getByte("test");
    }
}
