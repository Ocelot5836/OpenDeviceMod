package com.ocelot.opendevices.api.computer.application;

import com.google.gson.*;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TrayItemInfo;
import com.ocelot.opendevices.api.registry.DeviceRegistries;
import com.ocelot.opendevices.core.computer.AppInfoImpl;
import com.ocelot.opendevices.core.computer.TrayItemInfoImpl;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import com.ocelot.opendevices.core.registry.TrayItemRegistryEntry;
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
import java.util.Set;
import java.util.function.Function;

/**
 * <p>Manages the loading and parsing of {@link AppInfo} and {@link TrayItemInfo} JSONs.</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ApplicationManager
{
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).registerTypeAdapter(AppInfoImpl.class, new AppInfoDeserializer()).registerTypeAdapter(TrayItemInfoImpl.class, new TrayIconInfoDeserializer()).create();
    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();
    private static final Map<ResourceLocation, TrayItemInfo> TRAY_ITEM_INFO = new HashMap<>();

    private static AppInfo loadAppInfo(ResourceLocation registryName) throws IOException
    {
        String path = "/data/" + registryName.getNamespace() + "/apps/" + registryName.getPath() + ".json";
        InputStream stream = ApplicationManager.class.getResourceAsStream(path);
        if (stream == null)
            throw new FileNotFoundException("Could not locate app info file for '" + registryName + "' at '" + path + "'");
        return GSON.fromJson(IOUtils.toString(stream, StandardCharsets.UTF_8), AppInfoImpl.class);
    }

    private static TrayItemInfo loadTrayIconInfo(Function<Computer, Boolean> clickListener, ResourceLocation registryName) throws IOException
    {
        String path = "/data/" + registryName.getNamespace() + "/tray_items/" + registryName.getPath() + ".json";
        InputStream stream = ApplicationManager.class.getResourceAsStream(path);
        if (stream == null)
            throw new FileNotFoundException("Could not locate tray icon info file for '" + registryName + "' at '" + path + "'");
        return GSON.fromJson(IOUtils.toString(stream, StandardCharsets.UTF_8), TrayItemInfoImpl.class).setClickListener(clickListener);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerApplications(RegistryEvent.Register<ApplicationRegistryEntry> event)
    {
        for (Map.Entry<ResourceLocation, ApplicationRegistryEntry> entry : event.getRegistry().getEntries())
        {
            ResourceLocation registryName = entry.getKey();

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerTrayItems(RegistryEvent.Register<TrayItemRegistryEntry> event)
    {
        for (Map.Entry<ResourceLocation, TrayItemRegistryEntry> entry : event.getRegistry().getEntries())
        {
            ResourceLocation registryName = entry.getKey();

            try
            {
                TRAY_ITEM_INFO.put(registryName, loadTrayIconInfo(entry.getValue().getClickListener(), registryName));
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not load tray icon info JSON for tray icon '" + registryName + "'. Using Missing Tray Icon Info!", e);
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

    /**
     * @return The registry names of each application registered
     */
    public static Set<ResourceLocation> getApplications()
    {
        return APP_INFO.keySet();
    }

    /**
     * Checks the loaded tray icon infos for the specified tray icon.
     *
     * @param registryName The id of the tray icon to get the info for
     * @return The info found or {@link TrayItemInfo#EMPTY} if there was no associated info
     */
    public static TrayItemInfo getTrayItemInfo(ResourceLocation registryName)
    {
        return TRAY_ITEM_INFO.getOrDefault(registryName, TrayItemInfo.EMPTY);
    }

    /**
     * @return The registry names of each tray icon registered
     */
    public static Set<ResourceLocation> getTrayIcons()
    {
        return TRAY_ITEM_INFO.keySet();
    }

    private static class AppInfoDeserializer implements JsonDeserializer<AppInfoImpl>
    {
        @Override
        public AppInfoImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
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

            return new AppInfoImpl(name, description, !author.getFormattedText().isEmpty() ? new ITextComponent[]{author} : authors, version, icon);
        }
    }

    private static class TrayIconInfoDeserializer implements JsonDeserializer<TrayItemInfoImpl>
    {
        @Override
        public TrayItemInfoImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = json.getAsJsonObject();
            ITextComponent name = JSONUtils.deserializeClass(jsonObject, "name", context, ITextComponent.class);
            ResourceLocation icon = JSONUtils.deserializeClass(jsonObject, "icon", null, context, ResourceLocation.class);
            return new TrayItemInfoImpl(name, icon);
        }
    }
}
