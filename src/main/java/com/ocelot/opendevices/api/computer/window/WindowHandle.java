package com.ocelot.opendevices.api.computer.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.TaskBar;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Manages window creation and management automatically for a {@link DeviceProcess}.</p>
 *
 * @author Ocelot
 * @see DeviceProcess
 * @see WindowManager
 */
public class WindowHandle implements INBTSerializable<CompoundNBT>
{
    private WindowManager windowManager;
    private TaskBar taskBar;
    private UUID processId;
    private UUID windowId;

    public WindowHandle(WindowManager windowManager, TaskBar taskBar, UUID processId)
    {
        this.windowManager = windowManager;
        this.taskBar = taskBar;
        this.processId = processId;
    }

    /**
     * Creates a new window if there is not one.
     *
     * @return Whether or not a new window was actually created
     */
    public boolean create()
    {
        Window window = this.windowManager.getWindow(this.windowId);

        if (window == null)
        {
            this.windowId = null;
        }
        else
        {
            return false;
        }

        this.windowId = this.windowManager.openWindow(this.processId);
        return true;
    }

    /**
     * Closes this window if there is one. Does not request close!
     */
    public void close()
    {
        if (this.windowId != null)
        {
            this.windowManager.closeWindows(this.windowId);
            this.windowId = null;
        }
    }

    /**
     * Closes this window if there is one. Does not request close!
     */
    public void requestClose()
    {
        if (this.windowId != null)
        {
            this.windowManager.requestCloseWindows(this.windowId);
            this.windowId = null;
        }
    }

    /**
     * @return The id of this window or null if there is no window
     */
    @Nullable
    public UUID getWindowId()
    {
        return windowId;
    }

    /**
     * Centers this window on the desktop of the laptop.
     */
    public void center()
    {
        this.setPosition((DeviceConstants.LAPTOP_SCREEN_WIDTH - this.getWidth()) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.taskBar.getHeight() - (this.getHeight() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f);
    }

    /**
     * Sets the position of the window with the specified id to the specified coordinates.
     *
     * @param x The new x position of the window
     * @param y The new y position of the window
     */
    public void setPosition(float x, float y)
    {
        if (this.windowId != null)
        {
            this.windowManager.setWindowPosition(this.windowId, x, y);
        }
    }

    /**
     * Sets the size of the window with the specified id to the specified values.
     *
     * @param width  The new x size of the window
     * @param height The new y size of the window
     */
    public void setSize(int width, int height)
    {
        if (this.windowId != null)
        {
            this.windowManager.setWindowSize(this.windowId, width, height);
        }
    }

    /**
     * Sets the title of the window with the specified id to the specified title.
     *
     * @param title The new title of the window
     */
    public void setTitle(String title)
    {
        if (this.windowId != null)
        {
            this.windowManager.setWindowTitle(this.windowId, title);
        }
    }

    /**
     * Sets the icon of the window with the specified id to the specified icon sprite.
     *
     * @param icon The new icon for the window
     */
    public void setIcon(ResourceLocation icon)
    {
        if (this.windowId != null)
        {
            this.windowManager.setWindowIcon(this.windowId, icon);
        }
    }

    /**
     * @return The x position of the window or -1 if there is no window
     */
    public float getX()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getX();
    }

    /**
     * @return The y position of the window or -1 if there is no window
     */
    public float getY()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getY();
    }

    /**
     * @return The width position of the window or -1 if there is no window
     */
    public float getWidth()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getWidth();
    }

    /**
     * @return The height position of the window or -1 if there is no window
     */
    public float getHeight()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getHeight();
    }

    /**
     * @return Whether or not the window is trying to close
     */
    public boolean isCloseRequested()
    {
        return this.windowManager.isCloseRequested(this.windowId);
    }

    /**
     * @return Whether or not this window exists
     */
    public boolean exists()
    {
        if (this.windowId == null)
            return false;
        return this.windowManager.getWindow(this.windowId) != null;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        if (this.windowId != null)
        {
            nbt.putUniqueId("windowId", this.windowId);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.windowId = nbt.hasUniqueId("windowId") ? nbt.getUniqueId("windowId") : null;
    }
}
