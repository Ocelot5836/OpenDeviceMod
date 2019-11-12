package com.ocelot.opendevices.api.laptop.window;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Contains any additional information about an {@link Application} above the registry name</p>
 *
 * @author Ocelot
 */
public class AppInfo
{
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AppInfo.class, new AppInfo.Deserializer()).create();

    private ResourceLocation registryName;

    private String name;
    private String author;
    private String description;
    private String version;
    private String icon;
    private String[] screenshots;
    private Support support;

    private AppInfo()
    {
    }

    AppInfo(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public void reload() throws IOException
    {
        InputStream stream = OpenDevices.class.getResourceAsStream("/assets/" + registryName.getNamespace() + "/apps/" + registryName.getPath() + ".json");
        name = null;
        author = null;
        description = null;
        version = null;
        icon = null;
        screenshots = null;
        support = null;

        if (stream == null)
            throw new IOException("Missing app info json for '" + registryName + "'");

        AppInfo info = GSON.fromJson(IOUtils.toString(stream, Charsets.UTF_8), AppInfo.class);
        name = info.name;
        author = info.author;
        description = info.description;
        version = info.version;
        icon = info.icon;
        screenshots = info.screenshots;
        support = info.support;
    }

    /**
     * @return The registry name of the application
     */
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    private static class Support
    {
        private String paypal;
        private String patreon;
        private String twitter;
        private String youtube;

        /**
         * @return The PayPal support link URL
         */
        public String getPaypal()
        {
            return paypal;
        }

        /**
         * @return The Patreon support link URL
         */
        public String getPatreon()
        {
            return patreon;
        }

        /**
         * @return The Twitter support link URL
         */
        public String getTwitter()
        {
            return twitter;
        }

        /**
         * @return The YouTube support link URL
         */
        public String getYoutube()
        {
            return youtube;
        }
    }

    private static class Deserializer implements JsonDeserializer<AppInfo>
    {
        private static final Pattern LANG = Pattern.compile("\\$\\{[a-z]+}");

        @Override
        public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        {
            AppInfo info = new AppInfo();
            try
            {
                JsonObject jsonObject = json.getAsJsonObject();
                info.name = convertToLocal(info, jsonObject.get("name").getAsString());
                info.author = convertToLocal(info, jsonObject.get("author").getAsString());
                info.description = convertToLocal(info, jsonObject.get("description").getAsString());
                info.version = jsonObject.get("version").getAsString();

                if (jsonObject.has("screenshots") && jsonObject.get("screenshots").isJsonArray())
                {
                    info.screenshots = context.deserialize(jsonObject.get("screenshots"), new TypeToken<String[]>() {}.getType());
                }

                if (jsonObject.has("icon") && jsonObject.get("icon").isJsonPrimitive())
                {
                    info.icon = jsonObject.get("icon").getAsString();
                }

                if (jsonObject.has("support") && jsonObject.get("support").getAsJsonObject().size() > 0)
                {
                    JsonObject supportObj = jsonObject.get("support").getAsJsonObject();
                    Support support = new Support();

                    if (supportObj.has("paypal"))
                    {
                        support.paypal = supportObj.get("paypal").getAsString();
                    }
                    if (supportObj.has("patreon"))
                    {
                        support.patreon = supportObj.get("patreon").getAsString();
                    }
                    if (supportObj.has("twitter"))
                    {
                        support.twitter = supportObj.get("twitter").getAsString();
                    }
                    if (supportObj.has("youtube"))
                    {
                        support.youtube = supportObj.get("youtube").getAsString();
                    }

                    info.support = support;
                }
            }
            catch (JsonParseException e)
            {
                OpenDevices.LOGGER.error("Malformed app info json for '" + info.getRegistryName() + "'", e);
            }

            return info;
        }

        private String convertToLocal(AppInfo info, String s)
        {
            Matcher m = LANG.matcher(s);
            while (m.find())
            {
                String found = m.group();
                s = s.replace(found, I18n.format("app." + info.getRegistryName().getNamespace() + "." + info.getRegistryName().getPath().replaceAll("/", ".") + "." + found.substring(2, found.length() - 1)));
            }
            return s;
        }
    }
}
