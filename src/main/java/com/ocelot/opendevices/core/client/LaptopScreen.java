package com.ocelot.opendevices.core.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.laptop.desktop.LaptopDesktop;
import com.ocelot.opendevices.api.laptop.desktop.LaptopDesktopBackground;
import com.ocelot.opendevices.api.render.RenderUtil;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.CloseLaptopTask;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class LaptopScreen extends Screen
{
    private LaptopTileEntity laptop;
    private String modVersion;

    public LaptopScreen(LaptopTileEntity laptop)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.laptop = laptop;

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

    @Override
    public void tick()
    {
        Minecraft minecraft = this.getMinecraft();

        if (!minecraft.player.isAlive() || this.laptop == null || this.laptop.isRemoved())
        {
            minecraft.player.closeScreen();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = this.getMinecraft();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        this.renderBackground();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.textureManager.bindTexture(Constants.LAPTOP_GUI);

        /* Physical Screen Position */
        int posX = (this.width - Constants.LAPTOP_DEVICE_WIDTH) / 2;
        int posY = (this.height - Constants.LAPTOP_DEVICE_HEIGHT) / 2;

        {
            /* Screen Corners */
            this.blit(posX, posY, 0, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-LEFT
            this.blit(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER + 1, 0, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // TOP-RIGHT
            this.blit(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-RIGHT
            this.blit(posX, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER); // BOTTOM-LEFT

            /* Screen Edges */
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY, Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // TOP
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_DEVICE_WIDTH - Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // RIGHT
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_DEVICE_HEIGHT - Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_BORDER, 1, Constants.LAPTOP_BORDER); // BOTTOM
            RenderUtil.drawRectWithTexture(posX, posY + Constants.LAPTOP_BORDER, 0, Constants.LAPTOP_BORDER + 1, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_HEIGHT, Constants.LAPTOP_BORDER, 1); // LEFT

            /* Screen Center */
            RenderUtil.drawRectWithTexture(posX + Constants.LAPTOP_BORDER, posY + Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_BORDER, Constants.LAPTOP_SCREEN_WIDTH, Constants.LAPTOP_SCREEN_HEIGHT, 1, 1);
        }

        /* Translate x and y to not include the border */
        posX += Constants.LAPTOP_BORDER;
        posY += Constants.LAPTOP_BORDER;

        LaptopDesktop desktop = this.laptop.getDesktop();

        /* Desktop Background */
        {
            LaptopDesktopBackground desktopBackground = desktop.getBackground();
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

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_mouseDragged_6_, double p_mouseDragged_8_)
    {
        System.out.println(p_mouseDragged_6_);
        return true;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void removed()
    {
        if (this.laptop != null)
        {
            TaskManager.sendTask(new CloseLaptopTask(this.laptop.getPos()));
        }
    }
}
