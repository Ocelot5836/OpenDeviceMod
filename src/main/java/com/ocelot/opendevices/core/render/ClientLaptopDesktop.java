package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.core.LaptopDesktop;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.window.Window;
import com.ocelot.opendevices.core.window.WindowClient;
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
public class ClientLaptopDesktop extends AbstractGui
{
    private LaptopTileEntity laptop;
    private LaptopDesktop desktop;
    private String modVersion;

    public ClientLaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.desktop = laptop.getDesktop();

        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(OpenDevices.MOD_ID);
        if (modContainer.isPresent())
        {
            ArtifactVersion version = modContainer.get().getModInfo().getVersion();
            this.modVersion = String.format("%s.%s.%s", version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion());
            if (!StringUtils.isNullOrEmpty(version.getQualifier()))
            {
                this.modVersion += " " + version.getQualifier();
            }
        }
        else
        {
            this.modVersion = I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version.unknown");
        }
    }

    public void render(Minecraft minecraft, FontRenderer fontRenderer, int posX, int posY, float partialTicks)
    {
        /* Desktop Background */
        {
            DesktopBackground desktopBackground = desktop.getBackground();
            if (!desktopBackground.isOnline())
            {
                assert desktopBackground.getLocation() != null;
                minecraft.getTextureManager().bindTexture(desktopBackground.getLocation());
                RenderUtil.drawRectWithTexture(posX, posY, desktopBackground.getU(), desktopBackground.getV(), Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_SCREEN_HEIGHT, desktopBackground.getWidth(), desktopBackground.getHeight(), desktopBackground.getImageWidth(), desktopBackground.getImageHeight());
            }
            else
            {
                assert desktopBackground.getUrl() != null;
                // TODO download and render online image
            }
        }

        if (!Constants.DEVELOPER_MODE)
        {
            drawString(fontRenderer, I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.version", this.modVersion), posX + 5, posY + 5, this.laptop.readSetting(Constants.DESKTOP_TEXT_COLOR));
        }
        else
        {
            drawString(fontRenderer, I18n.format("screen." + OpenDevices.MOD_ID + ".laptop.dev_version", this.modVersion), posX + 5, posY + 5, this.laptop.readSetting(Constants.DESKTOP_TEXT_COLOR));
        }

        /* Applications */
        Window[] windows = this.desktop.getWindows();
        for (Window window : windows)
        {
            if (window instanceof WindowClient)
            {
                ((WindowClient) window).render(posX, posY, this.laptop.readSetting(Constants.WINDOW_COLOR), partialTicks);
            }
        }

        /* Task bar */
        {
            minecraft.getTextureManager().bindTexture(Constants.WINDOW_LOCATION);
            int color = laptop.readSetting(Constants.TASKBAR_COLOR);
            GlStateManager.color4f(((color >> 16) & 0xff) / 255f, ((color >> 8) & 0xff) / 255f, (color & 0xff) / 255f, ((color >> 24) & 0xff) / 255f);
            RenderUtil.drawRectWithTexture(posX, posY + Constants.LAPTOP_SCREEN_HEIGHT - Constants.LAPTOP_TASK_BAR_HEIGHT, 0, 15, 1, Constants.LAPTOP_TASK_BAR_HEIGHT, 1, Constants.LAPTOP_TASK_BAR_HEIGHT);
            RenderUtil.drawRectWithTexture(posX + 1, posY + Constants.LAPTOP_SCREEN_HEIGHT - Constants.LAPTOP_TASK_BAR_HEIGHT, 1, 15, Constants.LAPTOP_SCREEN_WIDTH - 2, Constants.LAPTOP_TASK_BAR_HEIGHT, 1, Constants.LAPTOP_TASK_BAR_HEIGHT);
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_SCREEN_WIDTH - 1, posY + Constants.LAPTOP_SCREEN_HEIGHT - Constants.LAPTOP_TASK_BAR_HEIGHT, 2, 15, 1, Constants.LAPTOP_TASK_BAR_HEIGHT, 1, Constants.LAPTOP_TASK_BAR_HEIGHT);
            GlStateManager.color4f(1, 1, 1, 1);
        }
    }
}
