package com.ocelot.opendevices.api.laptop.application;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
public class ApplicationManager
{
    public static final ResourceLocation LOCATION_APP_ICON_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "textures/atlas/application_icons.png");

    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();
    private static final HashBiMap<ResourceLocation, Class<? extends Application>> REGISTRY = HashBiMap.create();
    private static AtlasTexture iconAtlas;
    private static boolean initialized = false;

    private ApplicationManager() {}

    private static void createAtlas()
    {
        iconAtlas = new AtlasTexture("textures/app/icon");
        Minecraft.getInstance().getTextureManager().loadTickableTexture(LOCATION_APP_ICON_TEXTURE, iconAtlas);
    }

    /**
     * This should never be used by the consumer. Core use only!
     */
    @SuppressWarnings("unchecked")
    public static void init()
    {
        if (initialized)
        {
            OpenDevices.LOGGER.warn("Attempted to initialize Client Application Manager even though it has already been initialized. This should NOT happen!");
            return;
        }

        ApplicationLoader.FOUND.forEach((registryName, className) ->
        {
            try
            {
                Class clazz = Class.forName(className);

                if (!Application.class.isAssignableFrom(clazz))
                    throw new IllegalArgumentException("Application: " + clazz + " does not extend Application. Skipping!");

                REGISTRY.put(registryName, (Class<Application>) clazz);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not bind application class " + className + " for client. Skipping!", e);
            }
        });

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener());

        initialized = true;
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
        if (!REGISTRY.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        try
        {
            return Objects.requireNonNull(REGISTRY.get(registryName)).newInstance();
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
     * @return The application info of that app
     */
    public static AppInfo getAppInfo(ResourceLocation registryName)
    {
        if (!APP_INFO.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        return APP_INFO.get(registryName);
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
     * Checks the registry for a registry name under the specified application class.
     *
     * @param clazz The class to get the registry name of
     * @return The registry name of that app
     */
    public static ResourceLocation getRegistryName(Class<? extends WindowContent> clazz)
    {
        if (!REGISTRY.containsValue(clazz))
        {
            throw new RuntimeException("Unregistered Application: " + clazz.getName() + ". Use WindowContent#Register annotations to register an application.");
        }

        return REGISTRY.inverse().get(clazz);
    }

    /**
     * Checks the registry for a class under the specified registry name.
     *
     * @param registryName The registry name of the application to get
     * @return The physical class of that app
     */
    public static Class<? extends Application> getApplicationClass(ResourceLocation registryName)
    {
        if (!REGISTRY.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        return REGISTRY.get(registryName);
    }

    private static class ReloadListener implements IFutureReloadListener
    {
        @Override
        public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
        {
            Set<ResourceLocation> iconLocations = new HashSet<>();
            CompletableFuture<?>[] completablefuture = (ApplicationLoader.REGISTRY == null ? ApplicationLoader.FOUND.keySet() : ApplicationLoader.REGISTRY.getKeys()).stream().map(registryName -> CompletableFuture.runAsync(() ->
            {
                reloadApplication(registryName);
                if (APP_INFO.containsKey(registryName))
                {
                    ResourceLocation iconLocation = APP_INFO.get(registryName).getIconLocation();
                    if (iconLocation != null)
                    {
                        iconLocations.add(iconLocation);
                    }
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
            if (!ApplicationLoader.REGISTRY.containsKey(registryName))
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
