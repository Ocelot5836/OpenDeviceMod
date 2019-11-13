package com.ocelot.opendevices.api.laptop.window.application;

import com.google.common.base.Charsets;
import com.ocelot.opendevices.OpenDevices;
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
 * <p>Manages all client information about applications.</p>
 * <p>Any additional information can be fetched using {@link #getAppInfo(ResourceLocation)}</p>
 * <p>Application icon sprites can be fetched using {@link #getAppIcon(ResourceLocation)}</p>
 *
 * @author Ocelot
 * @see AppInfo
 */
public class ClientApplicationManager
{
    public static final ResourceLocation LOCATION_APP_ICON_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "textures/atlas/application_icons.png");

    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();
    private static AtlasTexture iconAtlas;
    private static boolean initialized = false;

    private ClientApplicationManager() {}

    private static void createAtlas()
    {
        iconAtlas = new AtlasTexture("textures/app/icon");
        Minecraft.getInstance().getTextureManager().loadTickableTexture(LOCATION_APP_ICON_TEXTURE, iconAtlas);
    }

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void addListeners()
    {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener());
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

    private static class ReloadListener implements IFutureReloadListener
    {
        @Override
        public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
        {
            Set<ResourceLocation> iconLocations = new HashSet<>();
            CompletableFuture<?>[] completablefuture = ApplicationManager.getAllRegisteredApplications().stream().map((app) -> CompletableFuture.runAsync(() ->
            {
                reloadApplication(ApplicationManager.getRegistryName(app));
                ResourceLocation registryName = ApplicationManager.getRegistryName(app);
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
            if (!ApplicationManager.isValidApplication(registryName))
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
