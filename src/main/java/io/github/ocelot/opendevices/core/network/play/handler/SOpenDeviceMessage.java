package io.github.ocelot.opendevices.core.network.play.handler;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class SOpenDeviceMessage implements SonarMessage<IDeviceClientPlayNetworkHandler>
{
    private UUID address;
    private CompoundNBT data;

    public SOpenDeviceMessage()
    {
    }

    public SOpenDeviceMessage(UUID address, CompoundNBT data)
    {
        this.address = address;
        this.data = data;
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        this.address = buf.readUniqueId();
        this.data = buf.readCompoundTag();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeUniqueId(this.address);
        buf.writeCompoundTag(this.data);
    }

    @Override
    public void processPacket(IDeviceClientPlayNetworkHandler handler, NetworkEvent.Context ctx)
    {
        handler.processOpenDeviceScreen(this, ctx);
    }

    @OnlyIn(Dist.CLIENT)
    public UUID getAddress()
    {
        return address;
    }

    @OnlyIn(Dist.CLIENT)
    public CompoundNBT getData()
    {
        return data;
    }
}
