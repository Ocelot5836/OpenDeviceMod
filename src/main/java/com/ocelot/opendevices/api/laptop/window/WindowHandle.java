package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Computer;
import net.minecraft.nbt.CompoundNBT;
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
    private Computer computer;
    private UUID processId;
    private UUID windowId;

    public WindowHandle(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
    }

    /**
     * Creates a new window if there is not one.
     *
     * @return Whether or not a new window was actually created
     */
    public boolean create()
    {
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);

        if (window == null)
        {
            this.windowId = null;
        }
        else
        {
            return false;
        }

        this.windowId = windowManager.openWindow(this.processId);
        return true;
    }

    /**
     * Closes this window if there is one. Does not request close!
     */
    public void close()
    {
        if (this.windowId != null)
        {
            WindowManager windowManager = this.computer.getWindowManager();
            windowManager.closeWindows(this.windowId);
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
        this.setPosition((DeviceConstants.LAPTOP_SCREEN_WIDTH - this.getWidth()) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.computer.getTaskBar().getHeight() - (this.getHeight() + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f);
    }

    /**
     * Sets the position of the window with the specified id to the specified coordinates.
     *
     * @param x The new x position of the window
     * @param y The new y position of the window
     */
    public void setPosition(float x, float y)
    {
        WindowManager windowManager = this.computer.getWindowManager();
        if (this.windowId != null)
        {
            windowManager.setWindowPosition(this.windowId, x, y);
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
        WindowManager windowManager = this.computer.getWindowManager();
        if (this.windowId != null)
        {
            windowManager.setWindowSize(this.windowId, width, height);
        }
    }

    /**
     * @return The x position of the window or -1 if there is no window
     */
    public float getX()
    {
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getX();
    }

    /**
     * @return The y position of the window or -1 if there is no window
     */
    public float getY()
    {
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getY();
    }

    /**
     * @return The width position of the window or -1 if there is no window
     */
    public float getWidth()
    {
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getWidth();
    }

    /**
     * @return The height position of the window or -1 if there is no window
     */
    public float getHeight()
    {
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);
        return window == null ? -1 : window.getHeight();
    }

    /**
     * @return Whether or not the window is trying to close
     */
    public boolean isCloseRequested()
    {
        return this.computer.getWindowManager().isCloseRequested(this.windowId);
    }

    /**
     * @return Whether or not this window exists
     */
    public boolean exists()
    {
        if (this.windowId == null)
            return false;
        WindowManager windowManager = this.computer.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);
        return window != null;
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
