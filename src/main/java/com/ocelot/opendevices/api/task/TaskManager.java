package com.ocelot.opendevices.api.task;

import com.ocelot.opendevices.api.laptop.DeviceRegistries;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The Task Manager handles all {@link Task} related information.<p>
 * <p>To register a task, use the {@link Task.Register} annotation on the task class and extend {@link Task}.<p>
 * <p>To send a task from the client to the server, see {@link TaskManager#sendToServer(Task, TaskReceiver)}.<p>
 * <p>To send a task from the server to a client, see {@link TaskManager#sendTo(Task, TaskReceiver, ServerPlayerEntity)}.<p>
 *
 * @author Ocelot
 * @see Task
 */
public final class TaskManager
{
    private static final Map<Class<? extends Task>, ResourceLocation> REGISTRY_CACHE = new HashMap<>();

    private TaskManager() {}

    /**
     * Sends a task from the client to the server.
     *
     * @param task     The task to send to the server
     * @param receiver The way the task will be handled when it is being returned from the server
     */
    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(Task task, TaskReceiver receiver)
    {
        if (getRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.SERVER.noArg(), new MessageRequest(task, receiver));
    }

    /**
     * Sends a task from the server to the client. In order to send a task to <b>ALL</b> clients, {@link TaskManager#sendToAll(Task)} should be used instead.
     *
     * @param task     The task to send to the server
     * @param receiver The way the task will be handled when it is being sent to the client
     * @param player   The player to base the receiver around as the receiver
     */
    public static void sendTo(Task task, TaskReceiver receiver, ServerPlayerEntity player)
    {
        if (getRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        switch (receiver)
        {
            case ALL:
                throw new IllegalArgumentException("Task \'" + getRegistryName(task.getClass()) + "\' should use \'TaskManager#sendToAll\' instead of \'TaskManager#sendTo\' for receiver type \'" + TaskReceiver.ALL + "\'");
            case SENDER:
                DeviceMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageRequest(task, receiver));
                break;
            case NEARBY:
                DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageRequest(task, receiver));
                break;
            case SENDER_AND_NEARBY:
                DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageRequest(task, receiver));
                break;
        }
    }

    /**
     * Sends a task from the server to all clients.
     *
     * @param task The task to send to all clients
     */
    public static void sendToAll(Task task)
    {
        if (getRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageRequest(task, TaskReceiver.ALL));
    }

    /**
     * Creates a new task based on registry name.
     *
     * @param registryName The registry name of the task to make
     * @return The task created or null if there was an error
     */
    @Nullable
    public static Task createTask(ResourceLocation registryName)
    {
        TaskRegistryEntry entry = DeviceRegistries.TASKS.getValue(registryName);
        if (entry == null)
            throw new RuntimeException("Unregistered Task: " + registryName + ". Use Task annotation to register a task.");

        return entry.createTask();
    }

    /**
     * Checks the registry for a registry name under the specified task class.
     *
     * @param clazz The class to get the registry name of
     * @return The registry name of that task or null if the task is not registered
     */
    @Nullable
    public static ResourceLocation getRegistryName(Class<? extends Task> clazz)
    {
        if (DeviceRegistries.TASKS.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
        {
            for (Map.Entry<ResourceLocation, TaskRegistryEntry> entry : DeviceRegistries.TASKS.getEntries())
            {
                REGISTRY_CACHE.put(entry.getValue().getTaskClass(), entry.getKey());
            }
        }
        return REGISTRY_CACHE.get(clazz);
    }

    /**
     * The way a {@link Task} is sent to a client.
     *
     * @author Ocelot
     */
    public enum TaskReceiver
    {
        SENDER, NEARBY, SENDER_AND_NEARBY, ALL
    }
}
