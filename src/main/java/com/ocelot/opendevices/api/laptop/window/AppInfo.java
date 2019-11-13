package com.ocelot.opendevices.api.laptop.window;

import com.google.gson.*;
import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Contains any additional information about an {@link Application} above the registry name.</p>
 *
 * @author MrCrayfish
 */
public class AppInfo
{
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AppInfo.class, new AppInfo.Deserializer()).create();

    private ResourceLocation registryName;

    private String name;
    private String author;
    private String description;
    private String version;
    private ResourceLocation icon;
    private String[] screenshots;
    private String paypal;
    private String patreon;
    private String twitter;
    private String youtube;

    private AppInfo()
    {
    }

    /**
     * @return The registry name of the application
     */
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    /**
     * @return The display name of the application
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The author of the application
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * @return The description of the application
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return The version of the application
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @return The screenshots of the application for the App Store
     */
    @Nullable
    public String[] getScreenshots()
    {
        return screenshots;
    }

    /**
     * @return The icon resource location
     */
    @Nullable
    public ResourceLocation getIconLocation()
    {
        return icon;
    }

    /**
     * @return The PayPal support link URL
     */
    @Nullable
    public String getPaypal()
    {
        return paypal;
    }

    /**
     * @return The Patreon support link URL
     */
    @Nullable
    public String getPatreon()
    {
        return patreon;
    }

    /**
     * @return The Twitter support link URL
     */
    @Nullable
    public String getTwitter()
    {
        return twitter;
    }

    /**
     * @return The YouTube support link URL
     */
    @Nullable
    public String getYoutube()
    {
        return youtube;
    }

    /**
     * Sets the registry name to the one provided.
     *
     * @param registryName The new registry name of the info
     */
    void setRegistryName(@Nullable ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AppInfo)) return false;
        AppInfo appInfo = (AppInfo) o;
        return registryName.equals(appInfo.registryName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(registryName);
    }

    private static class Deserializer implements JsonDeserializer<AppInfo>
    {
        private static final Pattern LANG = Pattern.compile("\\$\\{[a-z]+}");

        @Override
        public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            AppInfo info = new AppInfo();

            JsonObject jsonObject = json.getAsJsonObject();
            info.name = convertToLocal(info, jsonObject.get("name").getAsString());
            info.author = convertToLocal(info, jsonObject.get("author").getAsString());
            info.description = convertToLocal(info, jsonObject.get("description").getAsString());
            info.version = jsonObject.get("version").getAsString();

            if (jsonObject.has("screenshots") && jsonObject.get("screenshots").isJsonArray())
            {
                info.screenshots = context.deserialize(jsonObject.get("screenshots"), String[].class);
            }

            if (jsonObject.has("icon") && jsonObject.get("icon").isJsonPrimitive())
            {
                info.icon = new ResourceLocation(jsonObject.get("icon").getAsString());
            }

            if (jsonObject.has("support") && jsonObject.get("support").getAsJsonObject().size() > 0)
            {
                JsonObject supportObj = jsonObject.get("support").getAsJsonObject();

                if (supportObj.has("paypal"))
                {
                    info.paypal = supportObj.get("paypal").getAsString();
                }
                if (supportObj.has("patreon"))
                {
                    info.patreon = supportObj.get("patreon").getAsString();
                }
                if (supportObj.has("twitter"))
                {
                    info.twitter = supportObj.get("twitter").getAsString();
                }
                if (supportObj.has("youtube"))
                {
                    info.youtube = supportObj.get("youtube").getAsString();
                }
            }

            return info;
        }

        private String convertToLocal(AppInfo info, String string)
        {
            Matcher matcher = LANG.matcher(string);
            while (matcher.find())
            {
                String found = matcher.group();
                string = string.replace(found, I18n.format("app." + info.getRegistryName().getNamespace() + "." + info.getRegistryName().getPath().replaceAll("/", ".") + "." + found.substring(2, found.length() - 1)));
            }
            return string;
        }
    }

    static AppInfo deserialize(ResourceLocation registryName, String json)
    {
        try
        {
            return GSON.fromJson(json, AppInfo.class);
        }
        catch (JsonParseException e)
        {
            OpenDevices.LOGGER.error("Malformed app info json for '" + registryName + "'. Using missing info.", e);
        }
        return createMissingInfo(registryName);
    }

    private static AppInfo createMissingInfo(ResourceLocation registryName)
    {
        AppInfo info = new AppInfo();
        info.setRegistryName(registryName);
        info.name = "missingno";
        info.author = "missingno";
        info.description = "Malformed App Info JSON!";
        info.version = "missingno";
        return info;
    }
}
