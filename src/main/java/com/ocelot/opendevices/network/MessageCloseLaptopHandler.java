package com.ocelot.opendevices.network;

import java.util.function.Supplier;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.device.laptop.settings.SettingsManager;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageCloseLaptopHandler
{
    public static void handle(MessageCloseLaptop msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                World world = player.world;
                BlockPos pos = msg.pos;
                if (world.getTileEntity(pos) instanceof LaptopTileEntity)
                {
                    LaptopTileEntity laptop = (LaptopTileEntity) world.getTileEntity(pos);
                    assert laptop != null;
                    laptop.stopView(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}