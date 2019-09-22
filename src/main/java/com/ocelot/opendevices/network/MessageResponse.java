package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MessageResponse
{
    int id;
    Task request;
    CompoundNBT nbt;

    private MessageResponse(int id, Task request, CompoundNBT nbt)
    {
        this.id = id;
        this.request = request;
        this.nbt = nbt;
    }

    public MessageResponse(int id, Task request)
    {
        this(id, request, new CompoundNBT());
    }

    public static void encode(MessageResponse msg, PacketBuffer buf)
    {
        buf.writeInt(msg.id);
        buf.writeBoolean(msg.request.isSucessful());
        msg.request.prepareResponse(msg.nbt);
        buf.writeCompoundTag(msg.nbt);
    }

    public static MessageResponse decode(PacketBuffer buf)
    {
        int id = buf.readInt();
        boolean successful = buf.readBoolean();
        Task task = TaskManager.getAndRemoveTask(id);
        if (task == null)
            throw new NullPointerException("Could not decode task with id: " + id + " as it was null!");
        if (successful)
            task.setSuccessful();
        return new MessageResponse(id, task, buf.readCompoundTag());
    }
}
