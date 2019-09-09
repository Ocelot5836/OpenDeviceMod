package com.ocelot.opendevices.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class MessageCloseLaptop
{
    BlockPos pos;

    public MessageCloseLaptop(BlockPos pos)
    {
        this.pos = pos;
    }

    public static void encode(MessageCloseLaptop msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.pos);
    }

    public static MessageCloseLaptop decode(PacketBuffer buf)
    {
        return new MessageCloseLaptop(buf.readBlockPos());
    }
}
