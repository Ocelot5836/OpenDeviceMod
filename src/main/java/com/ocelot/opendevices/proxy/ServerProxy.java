package com.ocelot.opendevices.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ServerProxy
{
    public void openGui(PlayerEntity player, GuiType gui, @Nullable BlockPos pos)
    {
    }

    public enum GuiType
    {
        LAPTOP
    }
}
