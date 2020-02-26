package com.ocelot.opendevices.api.device;

import com.ocelot.opendevices.OpenDevices;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Manages input and rendering handlers for {@link DeviceProcess}.</p>
 * <p>To bind an input handler to a process, use {@link #bindInputHandler(Class, ProcessInputHandler)}.</p>
 * <p>To bind a window rendering handler to a process, use {@link #bindWindowRenderer(Class, ProcessWindowRenderer)}.</p>
 *
 * @author Ocelot
 * @see ProcessInputHandler
 * @see ProcessWindowRenderer
 */
public class ProcessInputRegistry
{
    private static final Map<Class<? extends DeviceProcess<? extends Device>>, ProcessInputHandler<?, ?>> INPUT_REGISTRY = new HashMap<>();
    private static final Map<Class<? extends DeviceProcess<? extends Device>>, ProcessWindowRenderer<?, ?>> RENDER_REGISTRY = new HashMap<>();

    /**
     * Binds an input handler to the specified process class.
     *
     * @param processClass The class of the process to bind to
     * @param inputHandler The input handler to attach to the process
     * @param <D>          The type of device the process is using
     */
    public static <D extends Device> void bindInputHandler(Class<? extends DeviceProcess<? extends Device>> processClass, ProcessInputHandler<D, ? extends DeviceProcess<D>> inputHandler)
    {
        INPUT_REGISTRY.put(processClass, inputHandler);
    }

    /**
     * Binds a window renderer handler to the specified process class.
     *
     * @param processClass The class of the process to bind to
     * @param inputHandler The window renderer to attach to the process
     * @param <D>          The type of device the process is using
     */
    public static <D extends Device> void bindWindowRenderer(Class<? extends DeviceProcess<? extends Device>> processClass, ProcessWindowRenderer<D, ? extends DeviceProcess<D>> inputHandler)
    {
        RENDER_REGISTRY.put(processClass, inputHandler);
    }

    /**
     * Gets the input handler from the specified process class or null if there is no input handler.
     *
     * @param process The process to fetch the input handler for
     * @param <D>     The type of device the process is using
     * @return The input handler found or null if there is not one
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <D extends Device> ProcessInputHandler<D, DeviceProcess<D>> getInputHandler(DeviceProcess<D> process)
    {
        if (!INPUT_REGISTRY.containsKey(process.getClass()))
            return null;
        try
        {
            return (ProcessInputHandler<D, DeviceProcess<D>>) INPUT_REGISTRY.get(process.getClass());
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not fetch process input handler for process class: " + process.getClass() + ".");
        }
        return null;
    }

    /**
     * Gets the window renderer from the specified process class or null if there is no window renderer.
     *
     * @param process The process to fetch the window renderer for
     * @param <D>     The type of device the process is using
     * @return The window renderer found or null if there is not one
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <D extends Device> ProcessWindowRenderer<D, DeviceProcess<D>> getWindowRenderer(DeviceProcess<D> process)
    {
        if (!RENDER_REGISTRY.containsKey(process.getClass()))
            return null;
        try
        {
            return (ProcessWindowRenderer<D, DeviceProcess<D>>) RENDER_REGISTRY.get(process.getClass());
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not fetch process window renderer for process class: " + process.getClass() + ".");
        }
        return null;
    }
}
