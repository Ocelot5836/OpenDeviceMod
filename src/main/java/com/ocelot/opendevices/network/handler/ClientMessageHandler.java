package com.ocelot.opendevices.network.handler;

import com.ocelot.opendevices.api.registry.DeviceRegistries;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.render.LaptopScreen;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageResponse;
import com.ocelot.opendevices.network.MessageOpenGui;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ClientMessageHandler implements MessageHandler
{
    public static final MessageHandler INSTANCE = new ClientMessageHandler();

    private ClientMessageHandler() {}

    @Override
    public void handleOpenGuiMessage(MessageOpenGui msg, Supplier<NetworkEvent.Context> ctx)
    {
        Minecraft minecraft = Minecraft.getInstance();
        World world = minecraft.world;

        ctx.get().enqueueWork(() ->
        {
            BlockPos pos = msg.getPos();

            if (msg.getType() == GuiType.LAPTOP)
            {
                if (pos != null && world.getTileEntity(pos) instanceof LaptopTileEntity)
                {
                    minecraft.displayGuiScreen(new LaptopScreen((LaptopTileEntity) world.getTileEntity(pos)));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleRequestMessage(MessageRequest msg, Supplier<NetworkEvent.Context> ctx)
    {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;

        ctx.get().enqueueWork(() ->
        {
            Task request = msg.getRequest();
            CompoundNBT nbt = msg.getNbt();

            if (DeviceRegistries.getTaskRegistryName(request.getClass()) == null)
                throw new RuntimeException("Unregistered Task: " + request.getClass().getName() + ". Use Task annotation to register a task.");

            request.processRequest(nbt, player.world, player);
            if (msg.getReceiver().returnsToSender())
                DeviceMessages.INSTANCE.send(PacketDistributor.SERVER.noArg(), new MessageResponse(request, nbt));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void handleResponseMessage(MessageResponse msg, Supplier<NetworkEvent.Context> ctx)
    {
        Minecraft minecraft = Minecraft.getInstance();

        ctx.get().enqueueWork(() ->
        {
            Task request = msg.getRequest();

            if (DeviceRegistries.getTaskRegistryName(request.getClass()) == null)
                throw new RuntimeException("Unregistered Task: " + request.getClass().getName() + ". Use Task annotation to register a task.");

            request.processResponse(msg.getNbt(), minecraft.world, minecraft.player);
        });
        ctx.get().setPacketHandled(true);
    }
}
