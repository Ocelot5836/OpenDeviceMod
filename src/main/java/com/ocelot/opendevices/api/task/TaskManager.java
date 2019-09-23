package com.ocelot.opendevices.api.task;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.network.MessageRequest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>The Task Manager handles all {@link Task} related information.<p>
 * <p>To register a task, use the {@link Register} annotation on the task class and extend {@link Task}.<p>
 * <p>To send a task, see {@link TaskManager#sendTask(Task)}.<p>
 *
 * @author Ocelot
 * @see Task
 */
public final class TaskManager
{
    private static final Type AUTO_REGISTRY = Type.getType(TaskManager.Register.class);
    private static final HashBiMap<ResourceLocation, Class<? extends Task>> REGISTRY = HashBiMap.create();
    private static final Map<Integer, Task> requests = new HashMap<>();
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
                OpenDevices.LOGGER.warn("Registered task: " + registryName);
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
     * @param task The task to send
     */
    public static void sendTask(Task task)
    {
        if (!REGISTRY.containsValue(task.getClass()))
        {
            throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#Register annotation to register a task.");
        }

        int requestId = currentId++;
        requests.put(requestId, task);
        DeviceMessages.INSTANCE.sendToServer(new MessageRequest(requestId, task));
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
     * Gets the task by the specified ID and removed is.
     *
     * @param id The id to get the task of
     * @return The task using that id or null if there was no task
     */
    @Nullable
    public static Task getAndRemoveTask(int id)
    {
        return requests.remove(id);
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
     * @author Ocelot5836
     */
    public @interface Register
    {
        /**
         * @return The name of this task. Should be in the format of <code>modid:taskName</code>. <b><i>Will not register unless mod id is provided!</i></b>
         */
        String value();
    }
}
