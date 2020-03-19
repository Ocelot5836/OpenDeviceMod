package com.ocelot.opendevices.api.application;

import com.google.gson.*;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.core.computer.ApplicationInfo;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Manages the loading and parsing of {@link AppInfo} JSONs.</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ApplicationManager
{
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).registerTypeAdapter(AppInfo.class, new AppInfoDeserializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();

    private static AppInfo loadAppInfo(ResourceLocation registryName) throws IOException
    {
        String path = "/data/" + registryName.getNamespace() + "/apps/" + registryName.getPath() + ".json";
        InputStream stream = ApplicationManager.class.getResourceAsStream(path);
        if (stream == null)
            throw new FileNotFoundException("Could not locate app info file for '" + registryName + "' at '" + path + "'");
        return GSON.fromJson(IOUtils.toString(stream, StandardCharsets.UTF_8), AppInfo.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerApplications(RegistryEvent.Register<ApplicationRegistryEntry> event)
    {
        for (Map.Entry<ResourceLocation, ApplicationRegistryEntry> applicationEntry : event.getRegistry().getEntries())
        {
            ResourceLocation registryName = applicationEntry.getKey();

            try
            {
                APP_INFO.put(registryName, loadAppInfo(registryName));
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not load app info JSON for application '" + registryName + "'. Using Missing App Info!", e);
            }
        }
    }

    /**
     * Checks the loaded app infos for the specified application class.
     *
     * @param applicationClass The class to get the info for
     * @return The info found or {@link AppInfo#EMPTY} if there was no associated info
     */
    public static AppInfo getAppInfo(Class<? extends Application> applicationClass)
    {
        ResourceLocation registryName = DeviceRegistries.getApplicationRegistryName(applicationClass);
        return registryName == null ? AppInfo.EMPTY : getAppInfo(registryName);
    }

    /**
     * Checks the loaded app infos for the specified application.
     *
     * @param registryName The id of the application to get the info for
     * @return The info found or {@link AppInfo#EMPTY} if there was no associated info
     */
    public static AppInfo getAppInfo(ResourceLocation registryName)
    {
        return APP_INFO.getOrDefault(registryName, AppInfo.EMPTY);
    }

    private static class AppInfoDeserializer implements JsonDeserializer<AppInfo>
    {
        @Override
        public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("author") && jsonObject.has("authors"))
                throw new JsonParseException("Either 'author' or 'authors' may be present. Both are not allowed.");

            ITextComponent name = JSONUtils.deserializeClass(jsonObject, "name", context, ITextComponent.class);
            ITextComponent description = JSONUtils.deserializeClass(jsonObject, "description", context, ITextComponent.class);
            ITextComponent author = JSONUtils.deserializeClass(jsonObject, "author", new StringTextComponent(""), context, ITextComponent.class);
            ITextComponent[] authors = JSONUtils.deserializeClass(jsonObject, "authors", new ITextComponent[0], context, ITextComponent[].class);
            String version = JSONUtils.deserializeClass(jsonObject, "version", context, String.class);
            ResourceLocation icon = JSONUtils.deserializeClass(jsonObject, "icon", null, context, ResourceLocation.class);

            return new ApplicationInfo(name, description, !author.getFormattedText().isEmpty() ? new ITextComponent[]{author} : authors, version, icon);
        }
    }
}
