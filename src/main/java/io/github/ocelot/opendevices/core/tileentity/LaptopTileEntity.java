package io.github.ocelot.opendevices.core.tileentity;

import io.github.ocelot.opendevices.api.device.DeviceTileEntity;
import io.github.ocelot.opendevices.core.init.DeviceBlocks;
import net.minecraft.tileentity.TileEntityType;

public class LaptopTileEntity extends DeviceTileEntity
{
    public LaptopTileEntity()
    {
        super(DeviceBlocks.LAPTOP_TILE_ENTITY.get());
    }
}
