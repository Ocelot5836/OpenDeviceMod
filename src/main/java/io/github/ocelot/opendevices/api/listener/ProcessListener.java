package io.github.ocelot.opendevices.api.listener;

import io.github.ocelot.opendevices.api.device.process.ProcessManager;

import java.util.UUID;

/**
 * <p>Listens for start/stop events in {@link ProcessManager}.</p>
 *
 * @author Ocelot
 */
public interface ProcessListener extends Listener
{
    /**
     * Called just after a process has started.
     *
     * @param processId The id of the process started
     */
    void onProcessStart(UUID processId);

    /**
     * Called just after a process has stopped.
     *
     * @param processId The id of the process stopped
     */
    void onProcessStop(UUID processId);
}
