package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class MessageRequest
{
    private Task request;
    private CompoundNBT nbt;
    private TaskManager.TaskReceiver receiver;

    private MessageRequest(Task request, CompoundNBT nbt, TaskManager.TaskReceiver receiver)
    {
        this.request = request;
        this.nbt = nbt;
        this.receiver = receiver;
    }

    public MessageRequest(Task request, TaskManager.TaskReceiver receiver)
    {
        this(request, new CompoundNBT(), receiver);
    }

    public static void encode(MessageRequest msg, PacketBuffer buf)
    {
        buf.writeResourceLocation(Objects.requireNonNull(DeviceRegistries.getTaskRegistryName(msg.request.getClass()), "Could not encode task class: " + msg.request.getClass() + " as it is not registered!"));
        msg.request.prepareRequest(msg.nbt);
        buf.writeBoolean(msg.request.isSucessful());
        buf.writeCompoundTag(msg.nbt);
        buf.writeVarInt(msg.receiver.ordinal());
    }

    public static MessageRequest decode(PacketBuffer buf)
    {
        ResourceLocation registryName = buf.readResourceLocation();
        Task task = TaskManager.createTask(registryName);
        if (task == null)
            throw new NullPointerException("Could not decode task: " + registryName + " as it was null!");
        if(buf.readBoolean())
            task.setSuccessful();
        return new MessageRequest(task, buf.readCompoundTag(), TaskManager.TaskReceiver.values()[buf.readVarInt() % TaskManager.TaskReceiver.values().length]);
    }

    public Task getRequest()
    {
        return request;
    }

    public CompoundNBT getNbt()
    {
        return nbt;
    }

    public TaskManager.TaskReceiver getReceiver()
    {
        return receiver;
    }
}
