package com.ocelot.opendevices.api.handler;

import javax.annotation.Nullable;

/**
 * <p>Used to detect a response from an asynchronous task.</p>
 *
 * @param <T> The type of response returned in {@link #handle(T)}
 * @author Ocelot
 */
public interface Callback<T>
{
    /**
     * Handles any type of response.
     *
     * @param response the value from the response returned or null if the task was not successful
     */
    void handle(@Nullable T response);
}
