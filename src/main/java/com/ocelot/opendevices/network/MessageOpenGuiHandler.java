package com.ocelot.opendevices.network;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageOpenGuiHandler
{
    public static void handle(MessageOpenGui msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            OpenDevices.PROXY.openGui(Minecraft.getInstance().player, msg.type, msg.pos);
        });
        ctx.get().setPacketHandled(true);
    }
}