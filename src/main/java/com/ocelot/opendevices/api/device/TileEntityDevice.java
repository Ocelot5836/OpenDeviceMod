package com.ocelot.opendevices.api.device;

import net.minecraft.util.math.BlockPos;

/**
 * <p>A world device is a {@link Device} that exists in the world as a tile entity.</p>
 *
 * @author Ocelot
 */
public interface TileEntityDevice extends Device
{
    /**
     * @return The position of this device in the world
     */
    BlockPos getPos();
}
