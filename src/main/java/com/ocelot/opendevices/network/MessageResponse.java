package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

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
        buf.writeResourceLocation(Objects.requireNonNull(DeviceRegistries.getTaskRegistryName(msg.request.getClass()), "Could not encode task class: " + msg.request.getClass() + " as it is not registered!"));
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
