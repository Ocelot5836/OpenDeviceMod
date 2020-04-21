package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TaskbarIcon;
import com.ocelot.opendevices.api.computer.taskbar.TrayItem;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.device.process.ProcessInputHandler;
import com.ocelot.opendevices.api.device.process.ProcessInputRegistry;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.core.LaptopTaskBar;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.LaptopWindowManager;
import com.ocelot.opendevices.core.computer.LaptopWindow;
import com.ocelot.opendevices.core.task.CloseLaptopTask;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.ShapeRenderer;
import io.github.ocelot.client.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class LaptopScreen extends Screen implements TooltipRenderer
{
    private LaptopTileEntity laptop;
    private int posX;
    private int posY;
    private boolean clickable;
    private Window draggingWindow; // TODO make the dragging window move based on the mouse position and not how it moves

    public LaptopScreen(LaptopTileEntity laptop)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.laptop = laptop;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        this.posX = (width - DeviceConstants.LAPTOP_GUI_WIDTH) / 2;
        this.posY = (height - DeviceConstants.LAPTOP_GUI_HEIGHT) / 2;
        this.draggingWindow = null;
        this.clickable = false;
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
        /* Partial Ticks are still broken :/ */
        partialTicks = Minecraft.getInstance().getRenderPartialTicks();

        Minecraft minecraft = this.getMinecraft();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        this.renderBackground();
        GlStateManager.color4f(1, 1, 1, 1);

        minecraft.textureManager.bindTexture(DeviceConstants.LAPTOP_GUI);

        {
            IVertexBuilder buffer = ShapeRenderer.begin();

            /* Screen Corners */
            ShapeRenderer.drawRectWithTexture(buffer, this.posX, this.posY, 0, 0, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER);
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY, DeviceConstants.LAPTOP_GUI_BORDER + 1, 0, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER);
            ShapeRenderer.drawRectWithTexture(buffer, this.posX, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER);
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER);

            /* Screen Edges */
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY, DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_GUI_BORDER, 1, DeviceConstants.LAPTOP_GUI_BORDER);
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_WIDTH - DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_GUI_BORDER, 1);
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_HEIGHT - DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_GUI_BORDER, 1, DeviceConstants.LAPTOP_GUI_BORDER); // BOTTOM
            ShapeRenderer.drawRectWithTexture(buffer, this.posX, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, 0, DeviceConstants.LAPTOP_GUI_BORDER + 1, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_HEIGHT, DeviceConstants.LAPTOP_GUI_BORDER, 1); // LEFT

            /* Screen Center */
            ShapeRenderer.drawRectWithTexture(buffer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, 1, 1);

            ShapeRenderer.end();
        }

        ScissorHelper.framebufferHeight = 0;
        ScissorHelper.framebufferScale = 0;

        /* Renders the Content */
        LaptopRenderer.render(this.laptop, minecraft, fontRenderer, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, mouseX, mouseY, partialTicks);

        super.render(mouseX, mouseY, partialTicks);

        /* Render the Tooltips */
        LaptopRenderer.renderOverlay(this, this.laptop, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, mouseX, mouseY, partialTicks, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    @Override
    public void renderTooltip(ItemStack stack, int posX, int posY)
    {
        super.renderTooltip(stack, posX, posY);
    }

    @Override
    public void renderTooltip(String tooltip, int posX, int posY, FontRenderer fontRenderer)
    {
        this.renderTooltip(Collections.singletonList(tooltip), posX, posY, fontRenderer);
    }

    @Override
    public void renderComponentHoverEffect(ITextComponent textComponent, int posX, int posY)
    {
        super.renderComponentHoverEffect(textComponent, posX, posY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods)
    {
        if (this.draggingWindow == null)
        {
            LaptopWindowManager windowManager = this.laptop.getWindowManager();
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();
            if (focusedWindow != null)
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null && inputHandler.onKeyPressed(focusedProcess, focusedWindow, keyCode, scanCode, mods))
                    {
                        return true;
                    }
                }
            }
        }
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || this.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))
        {
            this.getMinecraft().player.closeScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, mods);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int mods)
    {
        if (this.draggingWindow == null)
        {
            LaptopWindowManager windowManager = this.laptop.getWindowManager();
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();
            if (focusedWindow != null)
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null && inputHandler.onKeyReleased(focusedProcess, focusedWindow, keyCode, scanCode, mods))
                    {
                        return true;
                    }
                }
            }
        }
        return super.keyReleased(keyCode, scanCode, mods);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        LaptopWindowManager windowManager = this.laptop.getWindowManager();
        LaptopTaskBar taskBar = this.laptop.getTaskBar();
        LaptopWindow[] windows = windowManager.getWindows();
        boolean loseFocus = true;

        if (!RenderUtil.isMouseInside(mouseX, mouseY, this.posX + DeviceConstants.LAPTOP_GUI_BORDER, this.posY + DeviceConstants.LAPTOP_GUI_BORDER + DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT))
        {
            if (this.draggingWindow == null)
            {
                for (int i = 0; i < windows.length; i++)
                {
                    if (!loseFocus)
                        break;

                    LaptopWindow window = windows[windows.length - i - 1];
                    if (window.isWithinButton(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), Minecraft.getInstance().getRenderPartialTicks()))
                    {
                        if (!windowManager.isCloseRequested(window.getId()))
                        {
                            windowManager.requestCloseWindows(window.getId());
                        }
                        return true;
                    }
                    if (window.isWithin(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), Minecraft.getInstance().getRenderPartialTicks()))
                    {
                        windowManager.focusWindow(window.getId());
                        loseFocus = false;
                    }
                    if (window.isWithinContent(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), Minecraft.getInstance().getRenderPartialTicks()))
                    {
                        DeviceProcess<Computer> process = this.laptop.getProcess(window.getProcessId());
                        if (process != null)
                        {
                            ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(process);
                            if (inputHandler != null && inputHandler.onMousePressed(process, window, mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), mouseButton))
                            {
                                return true;
                            }
                        }

                        return super.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                    else if (window.isWithinWindowBar(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), Minecraft.getInstance().getRenderPartialTicks()))
                    {
                        this.draggingWindow = window;
                        return true;
                    }
                    this.clickable = true;
                }
            }

            if (loseFocus)
            {
                windowManager.focusWindow(null);
            }
        }
        else
        {
            TaskbarIcon[] displayedIcons = taskBar.getDisplayedIcons();
            TrayItem[] trayItems = taskBar.getTrayItems();
            int height = DeviceConstants.LAPTOP_TASK_BAR_HEIGHT;

            for (int i = 0; i < displayedIcons.length; i++)
            {
                TaskbarIcon window = displayedIcons[i];
                double iconX = 13 * i;
                double iconY = (height - 8) / 2f;
                if (RenderUtil.isMouseInside(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), iconX + iconY - 2, DeviceConstants.LAPTOP_SCREEN_HEIGHT - height + iconY - 2, 12, 12))
                {
                    if (window.execute())
                        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    break;
                }
            }

            for (int i = 0; i < trayItems.length; i++)
            {
                TrayItem trayItem = trayItems[i];
                String name = trayItem.getInfo().getName().getFormattedText();
                double iconX = 10 * i;
                double iconY = (height - 8) / 2f;
                if (!StringUtils.isEmpty(name) && RenderUtil.isMouseInside(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), DeviceConstants.LAPTOP_SCREEN_WIDTH - 8 - (iconX + iconY), DeviceConstants.LAPTOP_SCREEN_HEIGHT - height + iconY, 8, 8))
                {
                    if (trayItem.getInfo().getClickListener().apply(this.laptop))
                        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        LaptopWindowManager windowManager = this.laptop.getWindowManager();
        if (this.draggingWindow != null)
        {
            this.draggingWindow = null;
            return true;
        }
        else if (this.clickable && windowManager.getFocusedWindow() != null)
        {
            this.clickable = false;
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();

            if (focusedWindow != null && focusedWindow.isWithinContent(mouseX, mouseY, Minecraft.getInstance().getRenderPartialTicks()))
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null && inputHandler.onMouseReleased(focusedProcess, focusedWindow, mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), mouseButton))
                    {
                        return true;
                    }
                }
            }
        }

        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.draggingWindow == null)
        {
            LaptopWindowManager windowManager = this.laptop.getWindowManager();
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();

            if (focusedWindow != null && focusedWindow.isWithinContent(mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), Minecraft.getInstance().getRenderPartialTicks()))
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null && inputHandler.onMouseScrolled(focusedProcess, focusedWindow, mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), amount))
                    {
                        return true;
                    }
                }
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        if (this.draggingWindow == null)
        {
            LaptopWindowManager windowManager = this.laptop.getWindowManager();
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();

            if (focusedWindow != null && focusedWindow.isWithinContent(mouseX, mouseY, Minecraft.getInstance().getRenderPartialTicks()))
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null)
                    {
                        inputHandler.onMouseMoved(focusedProcess, focusedWindow, mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER));
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        LaptopWindowManager windowManager = this.laptop.getWindowManager();
        if (this.draggingWindow != null)
        {
            windowManager.moveWindow(this.draggingWindow.getId(), (float) deltaX, (float) deltaY);
            return true;
        }
        else if (windowManager.getFocusedWindow() != null)
        {
            LaptopWindow focusedWindow = windowManager.getFocusedWindow();

            if (focusedWindow != null && focusedWindow.isWithinContent(mouseX, mouseY, Minecraft.getInstance().getRenderPartialTicks()))
            {
                DeviceProcess<Computer> focusedProcess = this.laptop.getProcess(focusedWindow.getProcessId());
                if (focusedProcess != null)
                {
                    ProcessInputHandler<Computer, DeviceProcess<Computer>> inputHandler = ProcessInputRegistry.getInputHandler(focusedProcess);
                    if (inputHandler != null && inputHandler.onMouseDragged(focusedProcess, focusedWindow, mouseX - (this.posX + DeviceConstants.LAPTOP_GUI_BORDER), mouseY - (this.posY + DeviceConstants.LAPTOP_GUI_BORDER), mouseButton, deltaX, deltaY))
                    {
                        return true;
                    }
                }
            }
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
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
            TaskManager.sendToServer(new CloseLaptopTask(this.laptop.getAddress()), TaskManager.TaskReceiver.NONE);
        }
    }
}
