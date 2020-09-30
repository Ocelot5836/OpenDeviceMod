package io.github.ocelot.opendevices.core.network.play.handler;

import net.minecraftforge.fml.network.NetworkEvent;

public interface IDeviceClientPlayNetworkHandler
{
    void processOpenDeviceScreen(SOpenDeviceMessage msg, NetworkEvent.Context ctx);
}
