package com.ocelot.opendevices.proxy;

import com.ocelot.opendevices.client.screen.LaptopScreen;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ClientProxy extends ServerProxy
{
    @Override
    public void openGui(PlayerEntity player, GuiType gui, @Nullable BlockPos pos)
    {
        switch (gui)
        {
            case LAPTOP:
            {
                if (pos != null && player.world.getTileEntity(pos) instanceof LaptopTileEntity)
                {
                    Minecraft.getInstance().displayGuiScreen(new LaptopScreen((LaptopTileEntity) player.world.getTileEntity(pos)));
                }
                break;
            }
        }
    }
}
