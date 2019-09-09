package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.api.handler.ResponseHandler;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An async way to make requests to the internet. Uses {@link ResponseHandler} for managing what to do with said data.
 *
 * @author Ocelot5836
 */
public class OnlineRequest
{
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor(r -> new Thread(r, "Online Request Thread"));

    /**
     * Adds a request to the queue. Use the handler to process the response you get from the URL connection.
     *
     * @param url     the URL to make a request to
     * @param handler the response handler for the request
     */
    public static void make(String url, ResponseHandler<InputStream> handler)
    {
		POOL.execute(() ->
        {
            try (CloseableHttpClient client = HttpClients.createDefault())
            {
                HttpGet get = new HttpGet(url);
                try (CloseableHttpResponse response = client.execute(get))
                {
                    handler.handle(true, IOUtils.toBufferedInputStream(response.getEntity().getContent()));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                handler.handle(false, null);
            }
        });
    }
}