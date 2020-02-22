package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.Laptop;
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

    public static void render(Laptop laptop, Minecraft minecraft, FontRenderer fontRenderer, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        TextureManager textureManager = minecraft.getTextureManager();
        Desktop desktop = laptop.getDesktop();
        WindowManager windowManager = laptop.getWindowManager();
        TaskBar taskBar = laptop.getTaskBar();

        /* Desktop Background */
        {
            DesktopBackground desktopBackground = desktop.getBackground();
            if (!desktopBackground.isOnline() && desktopBackground.getLocation() != null)
            {
                minecraft.getTextureManager().bindTexture(desktopBackground.getLocation());
                RenderUtil.drawRectWithTexture(posX, posY, desktopBackground.getU(), desktopBackground.getV(), DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, desktopBackground.getWidth(), desktopBackground.getHeight(), desktopBackground.getImageWidth(), desktopBackground.getImageHeight());
            }
            //            else if (desktopBackground.getUrl() != null)
            //            {
            //                 TODO download and render online image
            //            }
        }

        /* Version Text */
        if (DeviceConstants.DEVELOPER_MODE)
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_version", MOD_VERSION), posX + 5, posY + 5, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", posX + 5, posY + 18, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(laptop.getProcessIds().size() + " Processes Running", posX + 5, posY + 31, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(laptop.getWindowManager().getWindows().length + " Windows Opened", posX + 5, posY + 44, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }
        else
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version", MOD_VERSION), posX + 5, posY + 5, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }

        /* Applications */
        Window[] windows = windowManager.getWindows();
        for (Window window : windows)
        {
            int borderColor = windowManager.getFocusedWindowId() != null && windowManager.getFocusedWindowId() == window.getId() ? laptop.readSetting(LaptopSettings.FOCUSED_WINDOW_COLOR) : laptop.readSetting(LaptopSettings.WINDOW_COLOR);
            renderWindow(posX, posY, window, laptop.readSetting(LaptopSettings.WINDOW_COLOR), borderColor, partialTicks);
        }

        /* Task bar */
        {
            textureManager.bindTexture(DeviceConstants.WINDOW_LOCATION);
            int color = laptop.readSetting(LaptopSettings.TASKBAR_COLOR);
            int height = taskBar.getHeight();

            RenderUtil.glColor(0xff000000 | color);
            {
                /* Corners */
                RenderUtil.drawRectWithTexture(posX, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height, 0, 15, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - 1, 0, 17, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + DeviceConstants.LAPTOP_SCREEN_WIDTH - 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height, 2, 15, 1, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + DeviceConstants.LAPTOP_SCREEN_WIDTH - 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - 1, 2, 17, 1, 1, 1, 1);

                /* Edges */
                RenderUtil.drawRectWithTexture(posX, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height + 1, 0, 16, 1, height - 2, 1, 1);
                RenderUtil.drawRectWithTexture(posX + 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height, 1, 15, DeviceConstants.LAPTOP_SCREEN_WIDTH - 2, 1, 1, 1);
                RenderUtil.drawRectWithTexture(posX + DeviceConstants.LAPTOP_SCREEN_WIDTH - 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height + 1, 2, 16, 1, height - 2, 1, 1);
                RenderUtil.drawRectWithTexture(posX + 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - 1, 1, 17, DeviceConstants.LAPTOP_SCREEN_WIDTH - 2, 1, 1, 1);

                /* Center */
                RenderUtil.drawRectWithTexture(posX + 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - height + 1, 1, 16, DeviceConstants.LAPTOP_SCREEN_WIDTH - 2, height - 2, 1, 1);
            }
            GlStateManager.color4f(1, 1, 1, 1);

            //            {
            //                int size = taskBar.isEnlarged() ? 16 : 8;
            //                int i = 0;
            //
            //                for (Window value : taskBar.getDisplayedWindows())
            //                {
            //                    WindowClient window = (WindowClient) value;
            //                    TextureAtlasSprite icon = ApplicationManager.getAppIcon(window.getContentId());
            //                    textureManager.bindTexture(ApplicationManager.LOCATION_APP_ICON_TEXTURE);
            //                    RenderUtil.drawRectWithTexture(posX + 4 + (size + 4) * i, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight() + 4, size, size, icon);
            //                    i++;
            //                }
            //            }
        }
    }

    public static void renderOverlay(TooltipRenderer renderer, Laptop laptop, Minecraft minecraft, FontRenderer fontRenderer, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Desktop desktop = laptop.getDesktop();
        TaskBar taskBar = laptop.getTaskBar();

        //        /* Task bar */
        //        {
        //            int size = taskBar.isEnlarged() ? 16 : 8;
        //            int i = 0;
        //
        //            for (Window value : taskBar.getDisplayedWindows())
        //            {
        //                WindowClient window = (WindowClient) value;
        //                if (!StringUtils.isEmpty(window.getContent().getTitle()) && RenderUtil.isMouseInside(mouseX, mouseY, posX + 4 + (size + 4) * i, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight() + 4, posX + 4 + (size + 4) * i + size, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - taskBar.getHeight() + 4 + size))
        //                {
        //                    renderer.renderTooltip(window.getContent().getTitle(), mouseX, mouseY);
        //                    return;
        //                }
        //                i++;
        //            }
        //        }
        //
        //        /* Applications */
        //        {
        //            for (Window value : desktop.getWindows())
        //            {
        //                WindowClient window = (WindowClient) value;
        //                window.setScreenPosition(posX, posY);
        //                window.renderOverlay(renderer, mouseX, mouseY, partialTicks);
        //            }
        //        }
    }

    /**
     * Renders the specified window at the provided desktop coordinates.
     *
     * @param posX         The raw x position of the desktop
     * @param posY         The raw y position of the desktop
     * @param window       The window to render
     * @param color        The color of the window
     * @param borderColor  The color of the border of the window
     * @param partialTicks The percentage time from last tick and this tick
     */
    public static void renderWindow(int posX, int posY, Window window, int color, int borderColor, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
        RenderUtil.glColor(0xff000000 | borderColor);

        float windowX = window.getLastX() + (window.getX() - window.getLastX()) * partialTicks;
        float windowY = window.getLastY() + (window.getY() - window.getLastY()) * partialTicks;
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
