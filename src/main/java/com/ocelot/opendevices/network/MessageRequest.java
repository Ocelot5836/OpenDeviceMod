package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MessageRequest
{
    int id;
    Task request;
    CompoundNBT nbt;
    boolean returnToNearby;
    boolean returnToSender;

    private MessageRequest(int id, Task request, CompoundNBT nbt, boolean returnToNearby, boolean returnToSender)
    {
        this.id = id;
        this.request = request;
        this.nbt = nbt;
        this.returnToNearby = returnToNearby;
        this.returnToSender = returnToSender;
    }

    public MessageRequest(int id, Task request, boolean returnToNearby, boolean returnToSender)
    {
        this(id, request, new CompoundNBT(), returnToNearby, returnToSender);
    }

    //	@Override
    //	public IMessage onMessage(MessageRequest message, MessageContext ctx)
    //	{
    //		message.request.processRequest(message.nbt, ctx.getServerHandler().player.world, ctx.getServerHandler().player);
    //		return new MessageResponse(message.id, message.request);
    //	}

    public static void encode(MessageRequest msg, PacketBuffer buf)
    {
        buf.writeInt(msg.id);
        buf.writeResourceLocation(TaskManager.getRegistryName(msg.request.getClass()));
        msg.request.prepareRequest(msg.nbt);
        buf.writeCompoundTag(msg.nbt);
        buf.writeBoolean(msg.returnToNearby);
        buf.writeBoolean(msg.returnToSender);
    }

    public static MessageRequest decode(PacketBuffer buf)
    {
        int id = buf.readInt();
        ResourceLocation registryName = buf.readResourceLocation();
        Task task = TaskManager.createTask(registryName);
        if (task == null)
            throw new NullPointerException("Could not decode task: " + registryName + " as it was null!");
        return new MessageRequest(id, task, buf.readCompoundTag(), buf.readBoolean(), buf.readBoolean());
    }
}
