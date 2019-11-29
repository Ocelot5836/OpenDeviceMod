package com.ocelot.opendevices.api.handler;

/**
 * <p>Used to detect a response from an asynchronous task.</p>
 *
 * @param <T> The type of response returned in {@link #handle(boolean, T)}
 * @author Ocelot
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
