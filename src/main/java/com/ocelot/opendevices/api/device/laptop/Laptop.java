package com.ocelot.opendevices.api.device.laptop;

import com.ocelot.opendevices.core.laptop.LaptopSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface Laptop
{
    IWorld getWorld();

    BlockPos getPos();

    LaptopSettings getSettings();
}
