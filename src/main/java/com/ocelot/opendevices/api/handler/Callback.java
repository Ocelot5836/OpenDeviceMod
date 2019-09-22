package com.ocelot.opendevices.api.handler;

/**
 * Used to detect a response from an async task.
 *
 * @param <T> The type of value returned in {@link Callback#handle(boolean, T)}
 */
public interface Callback<T>
{
    /**
     * Handles any type of response.
     *
     * @param success  Whether or not the response was a success
     * @param response the value from the response returned. null if success is false
     */
    void handle(boolean success, T response);
}
