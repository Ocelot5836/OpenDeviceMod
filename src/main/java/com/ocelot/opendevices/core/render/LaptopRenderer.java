package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.core.laptop.window.WindowClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;

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
            MOD_VERSION = String.format("%s.%s.%s", version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion()) + (!StringUtils.isNullOrEmpty(version.getQualifier()) ? (" " + version.getQualifier()) : "");
        }
        else
        {
            MOD_VERSION = I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version.unknown");
        }
    }

    public static void render(Laptop laptop, Minecraft minecraft, FontRenderer fontRenderer, int posX, int posY, int mouseX, int mouseY, float partialTicks)
    {
        Desktop desktop = laptop.getDesktop();

        /* Desktop Background */
        {
            DesktopBackground desktopBackground = desktop.getBackground();
            if (!desktopBackground.isOnline() && desktopBackground.getLocation() != null)
            {
                minecraft.getTextureManager().bindTexture(desktopBackground.getLocation());
                RenderUtil.drawRectWithTexture(posX, posY, desktopBackground.getU(), desktopBackground.getV(), DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, desktopBackground.getWidth(), desktopBackground.getHeight(), desktopBackground.getImageWidth(), desktopBackground.getImageHeight());
            }
            else if (desktopBackground.getUrl() != null)
            {
                // TODO download and render online image
            }
        }

        /* Version Text */
        if (!DeviceConstants.DEVELOPER_MODE)
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version", MOD_VERSION), posX + 5, posY + 5, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }
        else
        {
            fontRenderer.drawStringWithShadow(I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_version", MOD_VERSION), posX + 5, posY + 5, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            fontRenderer.drawStringWithShadow(Minecraft.getDebugFPS() + " FPS", posX + 5, posY + 18, laptop.readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
        }

        /* Applications */
        Window[] windows = desktop.getWindows();
        for (int i = 0; i < windows.length; i++)
        {
            if (windows[i] instanceof WindowClient)
            {
                WindowClient window = (WindowClient) windows[i];
                window.setScreenPosition(posX, posY);
                window.render(mouseX, mouseY, laptop.readSetting(LaptopSettings.WINDOW_COLOR), partialTicks);
            }
        }

        /* Task bar */
        {
            minecraft.getTextureManager().bindTexture(DeviceConstants.WINDOW_LOCATION);
            int color = laptop.readSetting(LaptopSettings.TASKBAR_COLOR);
            GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, 1);
            RenderUtil.drawRectWithTexture(posX, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 0, 15, 1, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 1, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT);
            RenderUtil.drawRectWithTexture(posX + 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 1, 15, DeviceConstants.LAPTOP_SCREEN_WIDTH - 2, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 1, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT);
            RenderUtil.drawRectWithTexture(posX + DeviceConstants.LAPTOP_SCREEN_WIDTH - 1, posY + DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 2, 15, 1, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, 1, DeviceConstants.LAPTOP_TASK_BAR_HEIGHT);
            GlStateManager.color4f(1, 1, 1, 1);
        }
    }
}
