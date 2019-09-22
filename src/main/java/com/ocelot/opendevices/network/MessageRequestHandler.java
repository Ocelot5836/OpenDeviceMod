package com.ocelot.opendevices.network;

import com.ocelot.opendevices.init.DeviceMessages;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequestHandler
{
    public static void handle(MessageRequest msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                msg.request.processRequest(msg.nbt, player.world, player);
                DeviceMessages.INSTANCE.sendTo(new MessageResponse(msg.id, msg.request), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
