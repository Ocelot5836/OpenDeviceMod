package com.ocelot.opendevices.network;

import com.ocelot.opendevices.proxy.ServerProxy;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

// TODO remove this at some point
public class MessageOpenGui
{
    ServerProxy.GuiType type;
    BlockPos pos;

    public MessageOpenGui(ServerProxy.GuiType type, BlockPos pos)
    {
        this.type = type;
        this.pos = pos;
    }

    public static void encode(MessageOpenGui msg, PacketBuffer buf)
    {
        buf.writeInt(msg.type.ordinal());
        buf.writeBlockPos(msg.pos);
    }

    public static MessageOpenGui decode(PacketBuffer buf)
    {
        return new MessageOpenGui(ServerProxy.GuiType.values()[buf.readInt() % ServerProxy.GuiType.values().length], buf.readBlockPos());
    }
}
