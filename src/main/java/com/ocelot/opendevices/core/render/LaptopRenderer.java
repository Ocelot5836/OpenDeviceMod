package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.device.ProcessInputRegistry;
import com.ocelot.opendevices.api.device.ProcessWindowRenderer;
import com.ocelot.opendevices.api.laptop.Computer;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.taskbar.TaskBar;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowManager;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class LaptopRenderer extends AbstractGui
{
    public static final String MOD_VERSION;

    static
    {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(OpenDevices.MOD_ID);
        if (modContainer.isPresent())
        {
            ArtifactVersion version = modContainer.get().getModInfo().getVersion();
            MOD_VERSION = String.format("%s.%s.%s", version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion()) + (!StringUtils.isEmpty(version.getQualifier()) ? (" " + version.getQualifier()) : "");
        }
        else
        {
            MOD_VERSION = I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.unknown_version");
        }
    }

    public static void render(Computer computer, Minecraft minecraft, FontRenderer fontRenderer, int posX, int posY, int screenWidth, int screenHeight, int mouseX, int mouseY, float partialTicks)
    {
        TextureManager textureManager = minecraft.getTextureManager();
        Desktop desktop = computer.getDesktop();
        WindowManager windowManager = computer.getWindowManager();
        TaskBar taskBar = computer.getTaskBar();

        /* Desktop Background */
        {
            DesktopBackground desktopBackground = desktop.getBackground();
            if (!desktopBackground.isOnline() && desktopBackground.getLocation() != null)
            {
                textureManager.bindTexture(desktopBackground.getLocation());
                RenderUtil.drawRectWithTexture(posX, posY, desktopBackground.getU(), desktopBackground.getV(), screenWidth, screenHeight, desktopBackground.getWidth(), desktopBackground.getHeight(), desktopBackground.getImageWidth(), desktopBackground.getImageHeight());
            }
            //            else if (desktopBackground.getUrl() != null)
            //            {
            //                 TODO download and render online image
            //            }
        }

        /* Version Text */
        if (DeviceConstants.DEVELOPER_MODE)
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_version", MOD_VERSION), posX + 5, posY + 5, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", posX + 5, posY + 18, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(computer.getProcessIds().size() + " Processes Running", posX + 5, posY + 31, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(computer.getWindowManager().getWindows().length + " Windows Opened", posX + 5, posY + 44, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }
        else
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version", MOD_VERSION), posX + 5, posY + 5, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }

        /* Applications */
        Window[] windows = windowManager.getWindows();
        for (Window window : windows)
        {
            int borderColor = window.getId().equals(windowManager.getFocusedWindowId()) ? computer.readSetting(LaptopSettings.FOCUSED_WINDOW_COLOR) : computer.readSetting(LaptopSettings.WINDOW_COLOR);
            renderWindow(posX, posY, window, computer.readSetting(LaptopSettings.WINDOW_COLOR), borderColor, partialTicks);
            renderCloseButton(posX, posY, mouseX, mouseY, window, !windowManager.isCloseRequested(window.getId()), computer.readSetting(LaptopSettings.WINDOW_BUTTON_COLOR), partialTicks);

            DeviceProcess<Computer> process = computer.getProcess(window.getProcessId());
            if (process != null)
            {
                ProcessWindowRenderer<Computer, DeviceProcess<Computer>> renderer = ProcessInputRegistry.getWindowRenderer(process);
                if (renderer != null)
                {
                    renderer.render(process, window, posX, posY, mouseX, mouseY, partialTicks);
                }
            }
        }

        /* Task bar */
        {
            textureManager.bindTexture(DeviceConstants.WINDOW_LOCATION);
            int color = computer.readSetting(LaptopSettings.TASKBAR_COLOR);
            int height = taskBar.getHeight();

            RenderUtil.glColor(0xff000000 | color);
            {
                /* Corners */
                RenderUtil.drawRectWithTexture(posX, posY + screenHeight - height, 0, 15, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX, posY + screenHeight - 1, 0, 17, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + screenWidth - 1, posY + screenHeight - height, 2, 15, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + screenWidth - 1, posY + screenHeight - 1, 2, 17, 1, 1, 1, 1);

                /* Edges */
                RenderUtil.drawRectWithTexture(posX, posY + screenHeight - height + 1, 0, 16, 1, height - 2, 1, 1);
                RenderUtil.drawRectWithTexture(posX + 1, posY + screenHeight - height, 1, 15, screenWidth - 2, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + screenWidth - 1, posY + screenHeight - height + 1, 2, 16, 1, height - 2, 1, 1);
                RenderUtil.drawRectWithTexture(posX + 1, posY + screenHeight - 1, 1, 17, screenWidth - 2, 1, 1, 1);

                /* Center */
                RenderUtil.drawRectWithTexture(posX + 1, posY + screenHeight - height + 1, 1, 16, screenWidth - 2, height - 2, 1, 1);
            }
            GlStateManager.color4f(1, 1, 1, 1);

            {
                int size = taskBar.isEnlarged() ? 16 : 8;
                int i = 0;

                for (Window window : taskBar.getDisplayedWindows())
                {
                    // TextureAtlasSprite icon = ApplicationManager.getAppIcon(window.getContentId());
                    // textureManager.bindTexture(ApplicationManager.LOCATION_APP_ICON_TEXTURE);
                    TextureAtlasSprite icon = Minecraft.getInstance().getTextureMap().getAtlasSprite("minecraft:item/paper");
                    textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                    RenderUtil.drawRectWithTexture(posX + 4 + (size + 4) * i, posY + screenHeight - taskBar.getHeight() + 4, size, size, icon);
                    i++;
                }
            }
        }
    }

    public static void renderOverlay(TooltipRenderer renderer, Computer computer, int posX, int posY, int screenWidth, int screenHeight, int mouseX, int mouseY, float partialTicks)
    {
        WindowManager windowManager = computer.getWindowManager();
        TaskBar taskBar = computer.getTaskBar();

        /* Task bar */
        {
            int size = taskBar.isEnlarged() ? 16 : 8;
            int i = 0;

            for (Window window : taskBar.getDisplayedWindows())
            {
                String title = String.valueOf(window.getId());
                if (!StringUtils.isEmpty(title) && RenderUtil.isMouseInside(mouseX, mouseY, posX + 4 + (size + 4) * i, posY + screenHeight - taskBar.getHeight() + 4, posX + 4 + (size + 4) * i + size, posY + screenHeight - taskBar.getHeight() + 4 + size))
                {
                    renderer.renderTooltip(title, mouseX, mouseY);
                    return;
                }
                i++;
            }
        }

        /* Applications */
        {
            for (Window window : windowManager.getWindows())
            {
                DeviceProcess<Computer> process = computer.getProcess(window.getProcessId());
                if (process != null)
                {
                    ProcessWindowRenderer<Computer, DeviceProcess<Computer>> windowRenderer = ProcessInputRegistry.getWindowRenderer(process);
                    if (windowRenderer != null)
                    {
                        windowRenderer.renderOverlay(renderer, process, window, posX, posY, mouseX, mouseY, partialTicks);
                    }
                }
            }
        }
    }

    private static void renderCloseButton(float posX, float posY, int mouseX, int mouseY, Window window, boolean enabled, int color, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, 1);

        float windowX = window.getInterpolatedX(partialTicks) + window.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1;
        float windowY = window.getInterpolatedY(partialTicks) + 1;

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderUtil.drawRectWithTexture(windowX + posX, windowY + posY, 26 + (!enabled ? 0 : window.isWithinButton(mouseX - posX, mouseY - posY, partialTicks) ? 2 : 1) * DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, 0, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE);
    }

    private static void renderWindow(int posX, int posY, Window window, int color, int borderColor, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        RenderUtil.glColor(0xff000000 | borderColor);

        float windowX = window.getInterpolatedX(partialTicks);
        float windowY = window.getInterpolatedY(partialTicks);
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        /* Corners */
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY, 0, 0, 1, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 13, posY + windowY, 2, 0, 13, 13, 13, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 1, posY + windowY + windowHeight - 1, 14, 14, 1, 1, 1, 1);
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY + windowHeight - 1, 0, 14, 1, 1, 1, 1);

        /* Edges */
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY, 1, 0, windowWidth - 14, 13, 1, 13);
        RenderUtil.drawRectWithTexture(posX + windowX + windowWidth - 1, posY + windowY + 13, 14, 13, 1, windowHeight - 14, 1, 1);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + windowHeight - 1, 1, 14, windowWidth - 2, 1, 13, 1);
        RenderUtil.drawRectWithTexture(posX + windowX, posY + windowY + 13, 0, 13, 1, windowHeight - 14, 1, 1);

        /* Center */
        RenderUtil.glColor(0xff000000 | color);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + 13, 1, 13, windowWidth - 2, windowHeight - 14, 13, 1);

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
