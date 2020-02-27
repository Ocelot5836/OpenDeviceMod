package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MessageResponse
{
    private Task request;
    private CompoundNBT nbt;

    public MessageResponse(Task request, CompoundNBT nbt)
    {
        this.request = request;
        this.nbt = nbt;
    }

    public static void encode(MessageResponse msg, PacketBuffer buf)
    {
        ResourceLocation registryName = DeviceRegistries.getTaskRegistryName(msg.request.getClass());
        if (registryName == null)
            throw new NullPointerException("Could not encode task class: " + msg.request.getClass() + " as it is not registered!");
        buf.writeResourceLocation(registryName);
        msg.request.prepareResponse(msg.nbt);
        buf.writeBoolean(msg.request.isSucessful());
        buf.writeCompoundTag(msg.nbt);
    }

    public static MessageResponse decode(PacketBuffer buf)
    {
        ResourceLocation registryName = buf.readResourceLocation();
        Task task = TaskManager.createTask(registryName);
        if (task == null)
            throw new NullPointerException("Could not decode task: " + registryName + " as it was null!");
        if (buf.readBoolean())
            task.setSuccessful();
        return new MessageResponse(task, buf.readCompoundTag());
    }

    public Task getRequest()
    {
        return request;
    }

    public CompoundNBT getNbt()
    {
        return nbt;
    }
}
