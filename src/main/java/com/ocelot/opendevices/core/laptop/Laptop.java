package com.ocelot.opendevices.core.laptop;

import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageCloseLaptop;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.util.math.BlockPos;

public class Laptop
{
    private LaptopTileEntity te;
    private BlockPos pos;

    public Laptop(LaptopTileEntity te)
    {
        this.te = te;
        this.pos = te.getPos();
    }

    public void close()
    {
        DeviceMessages.INSTANCE.sendToServer(new MessageCloseLaptop(this.pos));
    }

    public LaptopTileEntity getTileEntity()
    {
        return te;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
