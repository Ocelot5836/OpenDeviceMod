package com.ocelot.opendevices.api.laptop.application;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.DeviceRegistries;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>Manages all client information about applications such as the physical application classes.</p>
 * <p>Any additional information can be fetched using {@link #getAppInfo(ResourceLocation)}</p>
 * <p>Application icon sprites can be fetched using {@link #getAppIcon(ResourceLocation)}</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ApplicationManager
{
    public static final AppInfo MISSING_INFO = AppInfo.createMissingInfo(TextureManager.RESOURCE_LOCATION_EMPTY);
    public static final ResourceLocation LOCATION_APP_ICON_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "textures/atlas/application_icons.png");

    private static final HashBiMap<Class<? extends Application>, ResourceLocation> REGISTRY_CACHE = HashBiMap.create();
    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();
    private static AtlasTexture iconAtlas;

    private ApplicationManager() {}

    private static void createAtlas()
    {
        iconAtlas = new AtlasTexture("textures/app/icon");
        Minecraft.getInstance().getTextureManager().loadTickableTexture(LOCATION_APP_ICON_TEXTURE, iconAtlas);
    }

    @SuppressWarnings("unchecked")
    private static void fillCache()
    {
        for (Map.Entry<ResourceLocation, ApplicationRegistryEntry> entry : DeviceRegistries.APPLICATIONS.getEntries())
        {
            String className = entry.getValue().getApplicationClassName();
            try
            {
                Class applicationClass = Class.forName(className);

                if (!Application.class.isAssignableFrom(applicationClass))
                    throw new IllegalArgumentException("Application: " + applicationClass + " does not extend Application. Skipping!");

                REGISTRY_CACHE.put((Class<? extends Application>) applicationClass, entry.getKey());
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not bind application class " + className + " for client. Skipping!", e);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerApplications(RegistryEvent.Register<ApplicationRegistryEntry> event)
    {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener());
    }

    /**
     * Creates a new application based on registry name.
     *
     * @param registryName The registry name of the app to make
     * @return The app created or null if there was an error
     */
    @Nullable
    public static Application createApplication(ResourceLocation registryName)
    {
        if (!DeviceRegistries.APPLICATIONS.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        try
        {
            return getApplicationClass(registryName).newInstance();
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create application: " + registryName + ". Verify there is a public empty constructor.", e);
        }

        return null;
    }

    /**
     * Checks the registry for app info under the specified registry name.
     *
     * @param registryName The name of the application to get the info of
     * @return The application info of that app or null if there is no loaded info for
     */
    public static AppInfo getAppInfo(ResourceLocation registryName)
    {
        return APP_INFO.getOrDefault(registryName, MISSING_INFO);
    }

    /**
     * Checks the registry for app info under the specified registry name and fetches it's icon sprite.
     *
     * @param registryName The name of the application to get the info of
     * @return The icon sprite of that app or the missing sprite if it could not be found
     */
    public static TextureAtlasSprite getAppIcon(ResourceLocation registryName)
    {
        AppInfo info = getAppInfo(registryName);
        ResourceLocation iconLocation = info.getIconLocation() == null ? MissingTextureSprite.getLocation() : info.getIconLocation();
        return iconAtlas.getAtlasSprite(iconLocation.toString());
    }

    /**
     * Checks the registry for a class under the specified registry name.
     *
     * @param registryName The registry name of the application to get
     * @return The physical class of that app
     */
    public static Class<? extends Application> getApplicationClass(ResourceLocation registryName)
    {
        if (!DeviceRegistries.APPLICATIONS.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        if (REGISTRY_CACHE.isEmpty())
            fillCache();

        return REGISTRY_CACHE.inverse().get(registryName);
    }

    /**
     * Checks the registry for a registry name under the specified application class.
     *
     * @param clazz The class to get the registry name of
     * @return The registry name of that app
     */
    public static ResourceLocation getRegistryName(Class<? extends WindowContent> clazz)
    {
        if (DeviceRegistries.APPLICATIONS.isEmpty())
            return null;

        if (REGISTRY_CACHE.isEmpty())
            fillCache();

        return REGISTRY_CACHE.get(clazz);
    }

    private static class ReloadListener implements IFutureReloadListener
    {
        @Override
        public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
        {
            Set<ResourceLocation> iconLocations = new HashSet<>();
            CompletableFuture<?>[] completablefuture = DeviceRegistries.APPLICATIONS.getKeys().stream().map(registryName -> CompletableFuture.runAsync(() ->
            {
                reloadApplication(registryName);
                ResourceLocation iconLocation = getAppInfo(registryName).getIconLocation();
                if (iconLocation != null)
                {
                    iconLocations.add(iconLocation);
                }
            }, backgroundExecutor)).toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(completablefuture).thenApplyAsync(v ->
            {
                preparationsProfiler.startTick();
                preparationsProfiler.startSection("stitching");

                if (iconAtlas == null)
                {
                    createAtlas();
                }

                AtlasTexture.SheetData sheetData = iconAtlas.stitch(resourceManager, iconLocations, preparationsProfiler);

                preparationsProfiler.endSection();
                preparationsProfiler.endTick();
                return sheetData;
            }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(sheetData ->
            {
                reloadProfiler.startTick();
                reloadProfiler.startSection("upload");

                iconAtlas.upload(sheetData);

                reloadProfiler.endSection();
                reloadProfiler.endTick();
            }, gameExecutor);
        }

        private static void reloadApplication(ResourceLocation registryName)
        {
            if (!DeviceRegistries.APPLICATIONS.containsKey(registryName))
            {
                OpenDevices.LOGGER.warn("Attempted to reload unregistered application info for application '" + registryName + "'!");
                return;
            }

            try
            {
                InputStream stream = OpenDevices.class.getResourceAsStream("/assets/" + registryName.getNamespace() + "/apps/" + registryName.getPath() + ".json");
                if (stream == null)
                    throw new FileNotFoundException("Missing app info json. Should be located at 'assets/modid/apps/appId.json'");
                AppInfo info = AppInfo.deserialize(registryName, IOUtils.toString(stream, Charsets.UTF_8));
                info.setRegistryName(registryName);
                APP_INFO.put(registryName, info);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not create app info for '" + registryName + "'!", e);
            }
        }
    }
}
