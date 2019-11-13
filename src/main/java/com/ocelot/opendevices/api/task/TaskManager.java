package com.ocelot.opendevices.api.task;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>The Task Manager handles all {@link Task} related information.<p>
 * <p>To register a task, use the {@link Register} annotation on the task class and extend {@link Task}.<p>
 * <p>To send a client task, see {@link TaskManager#sendTask(Task, TaskReceiver)}.<p>
 * <p>To send a server task, see {@link TaskManager#sendTask(Task, TaskReceiver, ServerPlayerEntity)}.<p>
 *
 * @author Ocelot
 * @see Task
 */
public final class TaskManager
{
    private static final Type AUTO_REGISTRY = Type.getType(TaskManager.Register.class);
    private static final HashBiMap<ResourceLocation, Class<? extends Task>> REGISTRY = HashBiMap.create();
    private static int currentId = 0;
    private static boolean initialized = false;

    private TaskManager() {}

    /**
     * This should never be used by the consumer. Core use only!
     */
    @SuppressWarnings("unchecked")
    public static void init()
    {
        if (initialized)
        {
            OpenDevices.LOGGER.warn("Attempted to initialize Task Manager even though it has already been initialized. This should NOT happen!");
            return;
        }

        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(AUTO_REGISTRY)).collect(Collectors.toList());

        for (ModFileScanData.AnnotationData data : annotations)
        {
            ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

            String className = data.getClassType().getClassName();
            try
            {
                Class<?> clazz = Class.forName(className);

                if ("minecraft".equals(registryName.getNamespace()) || registryName.getPath().isEmpty())
                    throw new IllegalArgumentException("Task: " + clazz + " does not have a valid registry name. Skipping!");

                if (!Task.class.isAssignableFrom(clazz))
                    throw new IllegalArgumentException("Task: " + clazz + " does not extend Task. Skipping!");

                if (REGISTRY.containsKey(registryName))
                    throw new RuntimeException("Task: " + registryName + " attempted to override existing task. Skipping!");

                REGISTRY.put(registryName, (Class<? extends Task>) clazz);
                OpenDevices.LOGGER.debug("Registered task: " + registryName);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not register task class " + className + ". Skipping!", e);
            }
        }

        initialized = true;
    }

    /**
     * Sends a task from the client to the server.
     *
     * @param task     The task to send to the server
     * @param receiver The way the task will be handled when it is being returned from the server
     */
    @OnlyIn(Dist.CLIENT)
    public static void sendTask(Task task, TaskReceiver receiver)
    {
        if (!REGISTRY.containsValue(task.getClass()))
        {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#Register annotation to register a task.");
        }

        DeviceMessages.INSTANCE.send(PacketDistributor.SERVER.noArg(), new MessageRequest(task, receiver));
    }

    /**
     * Sends a task from the server to the client.
     *
     * @param task     The task to send to the server
     * @param receiver The way the task will be handled when it is being sent to the client
     * @param player   The player to base the receiver around as the receiver
     */
    public static void sendTask(Task task, TaskReceiver receiver, ServerPlayerEntity player)
    {
        if (!REGISTRY.containsValue(task.getClass()))
        {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#Register annotation to register a task.");
        }

        switch (receiver)
        {
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
     * Creates a new task based on registry name.
     *
     * @param registryName The registry name of the task to make
     * @return The task created or null if there was an error
     */
    @Nullable
    public static Task createTask(ResourceLocation registryName)
    {
        if (!REGISTRY.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Task: " + registryName + ". Use TaskManager#Register annotation to register a task.");
        }

        try
        {
            return Objects.requireNonNull(REGISTRY.get(registryName)).newInstance();
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create task: " + registryName + ". Verify there is a public empty constructor.", e);
        }

        return null;
    }

    /**
     * Checks the registry for a registry name under the specified task class.
     *
     * @param clazz The class to get the registry name of
     * @return The registry name of that task
     */
    public static ResourceLocation getRegistryName(Class<? extends Task> clazz)
    {
        if (!REGISTRY.containsValue(clazz))
        {
            throw new RuntimeException("Unregistered Task: " + clazz.getName() + ". Use TaskManager#Register annotation to register a task.");
        }

        return REGISTRY.inverse().get(clazz);
    }

    /**
     * Registers a new type of task that can be used in the {@link TaskManager}.
     *
     * @author Ocelot
     * @see Task
     */
    public @interface Register
    {
        /**
         * @return The name of this task. Should be in the format of <code>modid:taskName</code>. <b><i>Will not register unless mod id is provided!</i></b>
         */
        String value();
    }

    /**
     * The way a {@link Task} is sent to a client.
     *
     * @author Ocelot
     */
    public enum TaskReceiver
    {
        SENDER, NEARBY, SENDER_AND_NEARBY
    }
}
