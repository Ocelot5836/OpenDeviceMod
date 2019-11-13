package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.network.MessageClientResponse;
import com.ocelot.opendevices.network.MessageOpenGui;
import com.ocelot.opendevices.network.MessageRequest;
import com.ocelot.opendevices.network.handler.ClientMessageHandler;
import com.ocelot.opendevices.network.handler.MessageHandler;
import com.ocelot.opendevices.network.handler.ServerMessageHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeviceMessages
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(OpenDevices.MOD_ID, "instance"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static int index;

    public static void init()
    {
        registerMessage(MessageOpenGui.class, MessageOpenGui::encode, MessageOpenGui::decode, (msg, ctx) -> getHandler(ctx).handleOpenGuiMessage(msg, ctx));
        registerMessage(MessageRequest.class, MessageRequest::encode, MessageRequest::decode, (msg, ctx) -> getHandler(ctx).handleRequestMessage(msg, ctx));
        registerMessage(MessageClientResponse.class, MessageClientResponse::encode, MessageClientResponse::decode, (msg, ctx) -> getHandler(ctx).handleResponseMessage(msg, ctx));
    }

    private static <MSG> void registerMessage
            (Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(index++, messageType, encoder, decoder, messageConsumer);
    }

    private static MessageHandler getHandler(Supplier<NetworkEvent.Context> ctx)
    {
        return ctx.get().getDirection().getReceptionSide().isClient() ? ClientMessageHandler.INSTANCE : ServerMessageHandler.INSTANCE;
    }
}
