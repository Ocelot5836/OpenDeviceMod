package com.ocelot.opendevices.api.task;

import com.ocelot.opendevices.api.registry.DeviceRegistries;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The Task Manager handles all {@link Task} related information.</p>
 * <p>To register a task, use the {@link Task.Register} annotation on the task class and extend {@link Task}.</p>
 * <p>To send a task from the client to the server, see {@link #sendToServer(Task, TaskReceiver)}.</p>
 * <p>To send a task from the server to a client, see {@link #sendToTracking(Task, World, BlockPos)}</p>
 *
 * @author Ocelot
 * @see Task
 * @see TaskReceiver
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
        if (DeviceRegistries.getTaskRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.SERVER.noArg(), new MessageRequest(task, receiver));
    }

    //    /**
    //     * Sends a task from the server to the client. In order to send a task to <b>ALL</b> clients, the player should be null or the receiver set to {@link TaskReceiver#ALL}.
    //     *
    //     * @param task     The task to send to the server
    //     * @param receiver The way the task will be handled when it is being sent to the client
    //     * @param player   The player to base the receiver around as the receiver.
    //     */
    //    public static void sendToPlayer(Task task, TaskReceiver receiver, @Nullable ServerPlayerEntity player)
    //    {
    //        if (getRegistryName(task.getClass()) == null)
    //            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");
    //
    //        if (player == null || receiver == TaskReceiver.ALL)
    //        {
    //            DeviceMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageRequest(task, TaskReceiver.ALL));
    //            return;
    //        }
    //
    //        switch (receiver)
    //        {
    //            case SENDER:
    //                DeviceMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageRequest(task, receiver));
    //                break;
    //            case NEARBY:
    //                DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageRequest(task, receiver));
    //                break;
    //            case SENDER_AND_NEARBY:
    //                DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageRequest(task, receiver));
    //                break;
    //        }
    //    }

    /**
     * Sends a task from the server to the specified client.
     *
     * @param task           The task to send to the clients
     * @param player         The player to send the task to.
     * @param returnToSender Whether or not to return the task to the server after execution
     */
    public static void sendToClient(Task task, ServerPlayerEntity player, boolean returnToSender)
    {
        if (DeviceRegistries.getTaskRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageRequest(task, returnToSender ? TaskReceiver.SENDER : TaskReceiver.NONE));
    }

    /**
     * Sends a task from the server to all clients tracking the specified {@link Chunk}.
     *
     * @param task  The task to send to the clients
     * @param world The world to base the task from.
     * @param pos   The pos to base the task from.
     */
    public static void sendToTracking(Task task, World world, BlockPos pos)
    {
        if (DeviceRegistries.getTaskRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MessageRequest(task, TaskReceiver.NONE));
    }

    /**
     * Sends a task from the server to all clients.
     *
     * @param task  The task to send to the clients
     */
    public static void sendToAll(Task task)
    {
        if (DeviceRegistries.getTaskRegistryName(task.getClass()) == null)
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use Task annotation to register a task.");

        DeviceMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageRequest(task, TaskReceiver.NONE));
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
     * The way a {@link Task} is sent to a client.
     *
     * @author Ocelot
     */
    public enum TaskReceiver
    {
        /**
         * The server does not send a response at all
         */
        NONE,
        /**
         * The server sends a response to the one who sent the message
         */
        SENDER,
        /**
         * The server sends a response to nearby players except for the one who sent the message
         */
        NEARBY,
        /**
         * The server sends a response to ALL nearby players, including the one who sent the message
         */
        SENDER_AND_NEARBY,
        /**
         * The server sends a response to ALL players
         */
        ALL;

        /**
         * @return Whether or not a response should be sent to the client that sent a request
         */
        public boolean returnsToSender()
        {
            return this == SENDER || this == SENDER_AND_NEARBY || this == ALL;
        }
    }
}
