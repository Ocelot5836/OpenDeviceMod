package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.OpenDevices;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Manages inputs handlers for {@link DeviceProcess}.</p>
 * <p>To bind an input handler to a process, use {@link #bindInputHandler(Class, ProcessInputHandler)}.</p>
 *
 * @author Ocelot
 * @see ProcessInputHandler
 */
public class ProcessInputRegistry
{
    private static final Map<Class<DeviceProcess<? extends Device>>, ProcessInputHandler<?, ?>> REGISTRY = new HashMap<>();

    /**
     * Binds an input handler to the specified process class.
     *
     * @param processClass The class of the process to bind to
     * @param inputHandler The input handler to attach to the process
     * @param <D>          The type of device the process is using
     */
    public static <D extends Device> void bindInputHandler(Class<DeviceProcess<? extends Device>> processClass, ProcessInputHandler<D, DeviceProcess<D>> inputHandler)
    {
        if (REGISTRY.containsKey(processClass))
        {
            OpenDevices.LOGGER.error("Could not fetch bind input handler for process class: " + processClass + " as it already exists. Skipping!");
            return;
        }
        REGISTRY.put(processClass, inputHandler);
    }

    /**
     * Gets the input handler from the specified process class or null if there is no input handler.
     *
     * @param process The process to fetch the input handler for
     * @param <D>          The type of device the process is using
     * @return The input handler found or null if there is
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <D extends Device> ProcessInputHandler<D, DeviceProcess<D>> getInputHandler(DeviceProcess<D> process)
    {
        if (!REGISTRY.containsKey(process.getClass()))
            return null;
        try
        {
            return (ProcessInputHandler<D, DeviceProcess<D>>) REGISTRY.get(process.getClass());
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not fetch process input handler for process class: " + process.getClass() + ".");
        }
        return null;
    }
}
