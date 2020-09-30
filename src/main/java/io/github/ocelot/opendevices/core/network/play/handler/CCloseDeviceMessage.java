package io.github.ocelot.opendevices.core.network.play.handler;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CCloseDeviceMessage implements SonarMessage<IDeviceServerPlayNetworkHandler>
{
    private UUID address;

    public CCloseDeviceMessage()
    {
    }

    public CCloseDeviceMessage(UUID address)
    {
        this.address = address;
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        this.address = buf.readUniqueId();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeUniqueId(this.address);
    }

    @Override
    public void processPacket(IDeviceServerPlayNetworkHandler handler, NetworkEvent.Context ctx)
    {
        handler.processCloseDevice(this, ctx);
    }

    public UUID getAddress()
    {
        return address;
    }
}
