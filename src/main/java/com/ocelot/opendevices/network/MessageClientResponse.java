package com.ocelot.opendevices.network;

import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.task.TaskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MessageClientResponse
{
    private Task request;
    private CompoundNBT nbt;

    public MessageClientResponse(Task request, CompoundNBT nbt)
    {
        this.request = request;
        this.nbt = nbt;
    }

    public static void encode(MessageClientResponse msg, PacketBuffer buf)
    {
        buf.writeResourceLocation(TaskManager.getRegistryName(msg.request.getClass()));
        msg.request.prepareResponse(msg.nbt);
        buf.writeCompoundTag(msg.nbt);
    }

    public static MessageClientResponse decode(PacketBuffer buf)
    {
        ResourceLocation registryName = buf.readResourceLocation();
        Task task = TaskManager.createTask(registryName);
        if (task == null)
            throw new NullPointerException("Could not decode task: " + registryName + " as it was null!");
        return new MessageClientResponse(task, buf.readCompoundTag());
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
