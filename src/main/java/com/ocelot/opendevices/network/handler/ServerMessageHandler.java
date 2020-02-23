package com.ocelot.opendevices.network.handler;

import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageResponse;
import com.ocelot.opendevices.network.MessageOpenGui;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerMessageHandler implements MessageHandler
{
    public static final MessageHandler INSTANCE = new ServerMessageHandler();

    private ServerMessageHandler() {}

    @Override
    public void handleOpenGuiMessage(MessageOpenGui msg, Supplier<NetworkEvent.Context> ctx)
    {

    }

    @Override
    public void handleRequestMessage(MessageRequest msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                Task request = msg.getRequest();
                CompoundNBT nbt = msg.getNbt();

                if (TaskManager.getRegistryName(request.getClass()) == null)
                    throw new RuntimeException("Unregistered Task: " + request.getClass().getName() + ". Use Task annotation to register a task.");

                request.processRequest(nbt, player.world, player);
                switch (msg.getReceiver())
                {
                    case ALL:
                        DeviceMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageResponse(request, nbt));
                        break;
                    case SENDER:
                        DeviceMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageResponse(request, nbt));
                        break;
                    case NEARBY:
                        DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageResponse(request, nbt));
                        break;
                    case SENDER_AND_NEARBY:
                        DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageResponse(request, nbt));
                        break;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleResponseMessage(MessageResponse msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null)
            {
                Task request = msg.getRequest();

                if (TaskManager.getRegistryName(request.getClass()) == null)
                    throw new RuntimeException("Unregistered Task: " + request.getClass().getName() + ". Use Task annotation to register a task.");

                request.processResponse(msg.getNbt(), player.world, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
