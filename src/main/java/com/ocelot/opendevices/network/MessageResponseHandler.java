package com.ocelot.opendevices.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageResponseHandler
{
    public static void handle(MessageResponse msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> msg.request.processResponse(msg.nbt));
        ctx.get().setPacketHandled(true);
    }
}
