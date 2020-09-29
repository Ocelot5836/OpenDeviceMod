package com.ocelot.opendevices.network;

import com.ocelot.opendevices.network.handler.MessageHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

// TODO remove this at some point
public class MessageOpenGui
{
    private MessageHandler.GuiType type;
    private BlockPos pos;

    public MessageOpenGui(MessageHandler.GuiType type, BlockPos pos)
    {
        this.type = type;
        this.pos = pos;
    }

    public static void encode(MessageOpenGui msg, PacketBuffer buf)
    {
        buf.writeVarInt(msg.type.ordinal());
        buf.writeBlockPos(msg.pos);
    }

    public static MessageOpenGui decode(PacketBuffer buf)
    {
        return new MessageOpenGui(MessageHandler.GuiType.values()[buf.readVarInt() % MessageHandler.GuiType.values().length], buf.readBlockPos());
    }

    public MessageHandler.GuiType getType()
    {
        return type;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
