package com.ocelot.opendevices.network.handler;

import com.ocelot.opendevices.network.MessageResponse;
import com.ocelot.opendevices.network.MessageOpenGui;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface MessageHandler
{
    void handleOpenGuiMessage(MessageOpenGui msg, Supplier<NetworkEvent.Context> ctx);

    void handleRequestMessage(MessageRequest msg, Supplier<NetworkEvent.Context> ctx);

    void handleResponseMessage(MessageResponse msg, Supplier<NetworkEvent.Context> ctx);

    enum GuiType
    {
        LAPTOP
    }
}
