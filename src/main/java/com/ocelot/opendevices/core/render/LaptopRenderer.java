package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.component.SpinnerComponent;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.TaskBar;
import com.ocelot.opendevices.api.computer.desktop.Desktop;
import com.ocelot.opendevices.api.computer.desktop.DesktopBackground;
import com.ocelot.opendevices.api.computer.desktop.LocalDesktopBackground;
import com.ocelot.opendevices.api.computer.desktop.OnlineDesktopBackground;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.computer.window.WindowManager;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.device.process.ProcessInputRegistry;
import com.ocelot.opendevices.api.device.process.ProcessWindowRenderer;
import com.ocelot.opendevices.api.util.ImageFit;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    public static void update(Computer computer)
    {
        WindowManager windowManager = computer.getWindowManager();

        /* Windows */
        Window[] windows = windowManager.getWindows();
        for (Window window : windows)
        {
            DeviceProcess<Computer> process = computer.getProcess(window.getProcessId());
            if (process != null)
            {
                ProcessWindowRenderer<Computer, DeviceProcess<Computer>> renderer = ProcessInputRegistry.getWindowRenderer(process);
                if (renderer != null)
                {
                    renderer.update(process, window);
                }
            }
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
            DesktopBackground background = desktop.getBackground();
            switch (background.getType())
            {
                case RESOURCE_LOCATION:
                {
                    LocalDesktopBackground localDesktopBackground = (LocalDesktopBackground) background;
                    textureManager.bindTexture(localDesktopBackground.getLocation());
                    RenderUtil.drawRectWithTexture(posX, posY, background.getU(), background.getV(), screenWidth, screenHeight, background.getWidth(), background.getHeight(), background.getImageWidth(), background.getImageHeight(), background.getFit());
                    break;
                }
                case ONLINE:
                {
                    OnlineDesktopBackground onlineDesktopBackground = (OnlineDesktopBackground) background;
                    ResourceLocation location = onlineDesktopBackground.getLocation();
                    if (location != null)
                    {
                        textureManager.bindTexture(location);
                        RenderUtil.drawRectWithTexture(posX, posY, background.getU(), background.getV(), screenWidth, screenHeight, background.getWidth(), background.getHeight(), background.getImageWidth(), background.getImageHeight(), background.getFit());
                    }
                    else
                    {
                        onlineDesktopBackground.request();
                        SpinnerComponent.renderProgress(posX + (screenWidth - SpinnerComponent.SIZE) / 2f, posY + (screenHeight - SpinnerComponent.SIZE) / 2f, 0, 0xFFFFFFFF, onlineDesktopBackground.getProgress());
                    }
                    break;
                }
            }
        }

        /* Desktop Text */
        {
            int color = computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR);
            if (DeviceConstants.DEVELOPER_MODE)
            {
                fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_version", MOD_VERSION), posX + 5, posY + 5, color);
                fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_fps", Minecraft.getDebugFPS()), posX + 5, posY + 18, color);
                fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_processes", computer.getProcessIds().size()), posX + 5, posY + 31, color);
                fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_windows", computer.getWindowManager().getWindows().length), posX + 5, posY + 44, color);
            }
            else
            {
                fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version", MOD_VERSION), posX + 5, posY + 5, color);
            }
        }

        /* Windows */
        Window[] windows = windowManager.getWindows();
        for (Window window : windows)
        {
            int borderColor = window.getId().equals(windowManager.getFocusedWindowId()) ? computer.readSetting(LaptopSettings.FOCUSED_WINDOW_COLOR) : computer.readSetting(LaptopSettings.WINDOW_COLOR);
            renderWindow(posX, posY, window, computer.readSetting(LaptopSettings.WINDOW_COLOR), borderColor, partialTicks);
            renderCloseButton(posX, posY, mouseX, mouseY, window, !windowManager.isCloseRequested(window.getId()), computer.readSetting(LaptopSettings.WINDOW_BUTTON_COLOR), partialTicks);
            RenderUtil.drawStringClipped(fontRenderer, window.getTitle(), posX + window.getInterpolatedX(partialTicks) + 3, posY + window.getInterpolatedY(partialTicks) + 3, window.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 6, computer.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR), false);

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
            int highlightColor = computer.readSetting(LaptopSettings.TASKBAR_HIGHLIGHT_COLOR);
            int height = taskBar.getHeight();

            RenderUtil.glColor(color);
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

            /* Window Icons */
            {
                Window[] displayedWindows = taskBar.getDisplayedWindows();
                int size = taskBar.isEnlarged() ? 2 : 1;

                for (int i = 0; i < displayedWindows.length; i++)
                {
                    Window window = displayedWindows[i];
                    TextureAtlasSprite icon = IconManager.getWindowIcon(window.getIcon());
                    textureManager.bindTexture(IconManager.LOCATION_WINDOW_ICONS_TEXTURE);
                    RenderUtil.drawRectWithTexture(posX + 4 + (8 * size + 5) * i, posY + screenHeight - taskBar.getHeight() + 4, 8 * size, 8 * size, icon);
                    textureManager.bindTexture(DeviceConstants.WINDOW_LOCATION);
                    RenderUtil.glColor(highlightColor);
                    RenderUtil.drawRectWithTexture(posX + 2 + (8 * size + 5) * i, posY + screenHeight - taskBar.getHeight() + 2, 3, 15, 12 * size, 12 * size, 12, 12, 256, 256);
                    GlStateManager.color4f(1, 1, 1, 1);
                }
            }
        }
    }

    public static void renderOverlay(TooltipRenderer renderer, Computer computer, int posX, int posY, int screenWidth, int screenHeight, int mouseX, int mouseY, float partialTicks, ITooltipFlag flag)
    {
        WindowManager windowManager = computer.getWindowManager();
        TaskBar taskBar = computer.getTaskBar();

        /* Task bar */
        {
            Window[] displayedWindows = taskBar.getDisplayedWindows();
            int size = taskBar.isEnlarged() ? 2 : 1;

            for (int i = 0; i < displayedWindows.length; i++)
            {
                Window window = displayedWindows[i];
                String title = window.getTitle();
                if (!StringUtils.isEmpty(title) && RenderUtil.isMouseInside(mouseX, mouseY, posX + 2 + (8 * size + 5) * i, posY + screenHeight - taskBar.getHeight() + 2, posX + 2 + (8 * size + 5) * i + 12 * size, posY + screenHeight - taskBar.getHeight() + 2 + 12 * size))
                {
                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(title);
                    if (flag.isAdvanced())
                    {
                        DeviceProcess<Computer> process = computer.getProcess(window.getProcessId());
                        ResourceLocation registryName = process == null ? MissingTextureSprite.getLocation() : DeviceRegistries.getProcessRegistryName(process);
                        tooltip.add(TextFormatting.DARK_GRAY + String.valueOf(registryName == null ? MissingTextureSprite.getLocation() : registryName));
                    }
                    renderer.renderTooltip(tooltip, mouseX, mouseY);
                    return;
                }
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
        RenderUtil.glColor(color);

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
        RenderUtil.glColor(borderColor);

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
        RenderUtil.glColor(color);
        RenderUtil.drawRectWithTexture(posX + windowX + 1, posY + windowY + 13, 1, 13, windowWidth - 2, windowHeight - 14, 13, 1);

        GlStateManager.color4f(1, 1, 1, 1);
    }
}
