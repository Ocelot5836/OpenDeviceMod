package com.ocelot.opendevices.network;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.Minecraft;
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