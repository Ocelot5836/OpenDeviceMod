package io.github.ocelot.opendevices.core.network.play.handler;

import net.minecraftforge.fml.network.NetworkEvent;

public interface IDeviceServerPlayNetworkHandler
{
    void processCloseDevice(CCloseDeviceMessage msg, NetworkEvent.Context ctx);
}
