package com.ocelot.opendevices.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageResponseHandler
{
    public static void handle(MessageResponse msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> msg.request.processResponse(msg.nbt, Minecraft.getInstance().world, Minecraft.getInstance().player));
        ctx.get().setPacketHandled(true);
    }
}
