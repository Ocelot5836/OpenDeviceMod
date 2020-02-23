package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import com.ocelot.opendevices.core.task.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

public class LaptopWindowManager implements WindowManager, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private Stack<LaptopWindow> windows;
    private UUID focusedWindowId;

    public LaptopWindowManager(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.windows = new Stack<>();
        this.focusedWindowId = null;
    }

    private void checkBounds(LaptopWindow window)
    {
        if (window.getX() < 0)
            window.setX(0);
        if (window.getX() + window.getWidth() >= DeviceConstants.LAPTOP_SCREEN_WIDTH)
            window.setX(DeviceConstants.LAPTOP_SCREEN_WIDTH - window.getWidth());
        if (window.getY() < 0)
            window.setY(0);
        if (window.getY() + window.getHeight() >= DeviceConstants.LAPTOP_SCREEN_HEIGHT)
            window.setY(DeviceConstants.LAPTOP_SCREEN_HEIGHT - window.getHeight());
    }

    private LaptopWindow createWindow(UUID processId)
    {
        return new LaptopWindow(this.laptop, processId);
    }

    public LaptopWindow createWindow(CompoundNBT windowData)
    {
        return new LaptopWindow(this.laptop, windowData);
    }

    public void update()
    {
        this.windows.forEach(LaptopWindow::update);
    }

    public boolean syncOpenWindow(LaptopWindow window)
    {
        if (this.laptop.getProcess(window.getProcessId()) == null)
        {
            OpenDevices.LOGGER.warn("Could not open window under process with id '" + window.getProcessId() + "' as it does not exist. Skipping!");
            return false;
        }

        this.laptop.execute(() -> this.windows.push(window));
        return true;
    }

    public boolean syncFocusWindow(UUID windowId)
    {
        if (this.focusedWindowId == windowId)
            return false;

        LaptopWindow window = this.getWindow(windowId);

        if (windowId != null && window == null)
        {
            OpenDevices.LOGGER.warn("Could not focus window with id '" + windowId + "' as it does not exist. Skipping!");
            return false;
        }

        this.laptop.execute(() ->
        {
            this.focusedWindowId = windowId;
            if (window != null)
            {
                this.windows.removeElement(window);
                this.windows.push(window);
            }
        });
        return true;
    }

    public void syncCloseWindows(UUID... windowIds)
    {
        for (UUID windowId : windowIds)
        {
            LaptopWindow window = this.getWindow(windowId);
            if (window == null)
            {
                OpenDevices.LOGGER.warn("Could not close window with id '" + windowId + "' as it does not exist. Skipping!");
                continue;
            }
            if (this.focusedWindowId != null && window.getId() == windowId)
                this.focusedWindowId = null;
            this.windows.removeElement(window);
        }
    }

    public boolean syncMoveWindow(UUID windowId, float xDirection, float yDirection)
    {
        LaptopWindow window = this.getWindow(windowId);

        if (window == null)
        {
            OpenDevices.LOGGER.warn("Could not move window with id '" + windowId + "' as it does not exist. Skipping!");
            return false;
        }

        window.setX(window.getX() + xDirection);
        window.setY(window.getY() + yDirection);
        this.checkBounds(window);
        return true;
    }

    public boolean syncSetWindowPosition(UUID windowId, float x, float y)
    {
        LaptopWindow window = this.getWindow(windowId);

        if (window == null)
        {
            OpenDevices.LOGGER.warn("Could not set position of window with id '" + windowId + "' as it does not exist. Skipping!");
            return false;
        }

        window.setX(x);
        window.setY(y);
        window.setLastX(x);
        window.setLastY(y);
        this.checkBounds(window);
        return true;
    }

    public boolean syncSetWindowSize(UUID windowId, int width, int height)
    {
        LaptopWindow window = this.getWindow(windowId);

        if (window == null)
        {
            OpenDevices.LOGGER.warn("Could not set size of window with id '" + windowId + "' as it does not exist. Skipping!");
            return false;
        }

        window.setWidth(width);
        window.setHeight(height);
        return true;
    }

    @Override
    public UUID openWindow(UUID processId)
    {
        if (this.laptop.getProcess(processId) == null)
        {
            OpenDevices.LOGGER.warn("Could not open window under process with id '" + processId + "' as it does not exist. Skipping!");
            return null;
        }

        LaptopWindow window = this.createWindow(processId);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new OpenWindowTask(this.laptop.getPos(), window.serializeNBT()), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
        }
        else
        {
            if (this.syncOpenWindow(window))
            {
                TaskManager.sendToTracking(new OpenWindowTask(this.laptop.getPos(), window.serializeNBT()), this.laptop.getWorld(), this.laptop.getPos());
            }
        }
        return window.getId();
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {
        this.syncFocusWindow(windowId);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new FocusWindowTask(this.laptop.getPos(), windowId), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void closeProcessWindows(UUID processId)
    {
        this.closeWindows(this.windows.stream().filter(window -> window.getProcessId().equals(processId)).map(LaptopWindow::getProcessId).collect(Collectors.toSet()));
    }

    @Override
    public void closeWindows(Collection<UUID> windowIds)
    {
        this.closeWindows(windowIds.toArray(new UUID[0]));
    }

    @Override
    public void closeWindows(UUID... windowIds)
    {
        this.syncCloseWindows(windowIds);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new CloseWindowTask(this.laptop.getPos(), windowIds), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new CloseWindowTask(this.laptop.getPos(), windowIds), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void moveWindow(UUID windowId, float xDirection, float yDirection)
    {
        this.syncMoveWindow(windowId, xDirection, yDirection);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new MoveWindowTask(this.laptop.getPos(), windowId, xDirection, yDirection), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new MoveWindowTask(this.laptop.getPos(), windowId, xDirection, yDirection), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void setWindowPosition(UUID windowId, float x, float y)
    {
        this.syncSetWindowPosition(windowId, x, y);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new SetWindowPositionTask(this.laptop.getPos(), windowId, x, y), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SetWindowPositionTask(this.laptop.getPos(), windowId, x, y), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void setWindowSize(UUID windowId, int width, int height)
    {
        this.syncSetWindowSize(windowId, width, height);
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new SetWindowSizeTask(this.laptop.getPos(), windowId, width, height), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SetWindowSizeTask(this.laptop.getPos(), windowId, width, height), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Nullable
    @Override
    public LaptopWindow getWindow(UUID windowId)
    {
        if (windowId == null)
            return null;
        return this.windows.stream().filter(window -> window.getId().equals(windowId)).findFirst().orElse(null);
    }

    @Override
    public LaptopWindow[] getWindows()
    {
        return this.windows.toArray(new LaptopWindow[0]);
    }

    @Nullable
    public LaptopWindow getFocusedWindow()
    {
        return this.getFocusedWindowId() == null ? null : this.getWindow(this.getFocusedWindowId());
    }

    @Nullable
    @Override
    public Window getTopWindow()
    {
        return !this.windows.isEmpty() ? this.windows.lastElement() : null;
    }

    @Nullable
    @Override
    public UUID getFocusedWindowId()
    {
        return focusedWindowId;
    }

    @Nullable
    @Override
    public UUID getTopWindowId()
    {
        return !this.windows.isEmpty() ? this.windows.lastElement().getId() : null;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT windowsNbt = new ListNBT();
        this.windows.forEach(window -> windowsNbt.add(window.serializeNBT()));
        nbt.put("windows", windowsNbt);

        if (this.focusedWindowId != null)
        {
            nbt.putUniqueId("focusedWindowId", this.focusedWindowId);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.windows.clear();
        ListNBT windowsNbt = nbt.getList("windows", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < windowsNbt.size(); i++)
        {
            LaptopWindow window = this.createWindow(windowsNbt.getCompound(i));
            this.checkBounds(window);
            this.windows.push(window);
        }
        this.focusedWindowId = nbt.hasUniqueId("focusedWindowId") ? nbt.getUniqueId("focusedWindowId") : null;
    }
}
