package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.OpenDevices;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * <p>An asynchronous way to make requests to the internet using {@link #make(String, Consumer)}.</p>
 * <p>An alternate option is to use {@link #make(String)} which returns a {@link Future}. In order to wait until the data is downloaded, use <code>result = make(url).get()</code></p>
 *
 * @author Ocelot
 * @see Consumer
 * @see Future
 */
public class OnlineRequest
{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), task -> new Thread(task, "Online Request Pool"));

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(POOL::shutdown));
    }

    private OnlineRequest() {}

    private static InputStream request(String url)
    {
        try (CloseableHttpClient client = HttpClients.createDefault())
        {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get))
            {
                return IOUtils.toBufferedInputStream(response.getEntity().getContent());
            }
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not retrieve data from '" + url + "'", e);
            return null;
        }
    }

    /**
     * Adds a request to the queue. Use the handler to process the response fetched from the URL connection.
     *
     * @param url     the URL to make a request to
     * @param handler the response handler for the request
     */
    public static void make(String url, Consumer<InputStream> handler)
    {
        POOL.execute(() -> handler.accept(request(url)));
    }

    /**
     * Adds a request to the queue.
     *
     * @param url the URL to make a request to
     * @return A {@link Future} representing the resulting task of this.
     */
    public static Future<InputStream> make(String url)
    {
        return POOL.submit(() -> request(url));
    }
}
