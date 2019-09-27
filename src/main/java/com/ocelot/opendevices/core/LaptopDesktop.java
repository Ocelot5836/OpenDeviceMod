package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.CloseWindowTask;
import com.ocelot.opendevices.core.task.FocusWindowTask;
import com.ocelot.opendevices.core.task.OpenWindowTask;
import com.ocelot.opendevices.core.window.Window;
import com.ocelot.opendevices.core.window.WindowClient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;
    private Stack<Window> windows;
    private Window[] windowsArray;
    private UUID focusedWindowId;

    public LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
        this.windowsArray = new Window[DeviceConstants.MAX_OPEN_APPS];
        this.focusedWindowId = null;
    }

    public Window createWindow(float x, float y, int width, int height)
    {
        return DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop, x, y, width, height), () -> () -> new Window(this.laptop, x, y, width, height));
    }

    public Window createWindow(int width, int height)
    {
        return DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop, width, height), () -> () -> new Window(this.laptop, width, height));
    }

    public Window createWindow()
    {
        return DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop), () -> () -> new Window(this.laptop));
    }

    public void update()
    {
        for (Window window : this.windows)
        {
            window.update();
        }
    }

    /**
     * @deprecated For testing only, should be removed as soon as possible!
     */
    public void openApplicationTest()
    {
        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
        {
            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
            return;
        }

        TaskManager.sendTaskToNearby(new OpenWindowTask(this.laptop.getPos(), this.createWindow(200, 100)));
    }

    public void syncOpenWindow(Window window)
    {
        if (this.windows.stream().noneMatch(frame -> frame.equals(window)))
        {
            this.laptop.execute(() -> this.windows.push(window));
        }
    }

    public void syncFocusWindow(@Nullable UUID windowId)
    {
        if (this.focusedWindowId == windowId)
            return;

        Window lastFocusWindow = this.getWindow(this.focusedWindowId);
        Window window = this.getWindow(windowId);

        if (window != null)
        {
            this.windows.remove(window);
            this.windows.push(window);
            window.onGainFocus();
        }

        if (lastFocusWindow != null)
        {
            lastFocusWindow.onLostFocus();
        }
        this.focusedWindowId = windowId == null ? null : window != null ? windowId : null;
    }

    public void syncCloseWindow(Window window)
    {
        window.onClose();
        this.windows.remove(window);
        if (this.focusedWindowId != null && this.focusedWindowId.equals(window.getId()))
        {
            this.focusWindow(null);
        }
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {
        TaskManager.sendTaskToNearby(new FocusWindowTask(this.laptop.getPos(), windowId));
    }

    @Override
    public void closeAllWindows()
    {
        this.laptop.execute(() -> this.windows.forEach(window -> this.closeWindow(window.getId())));
    }

    @Override
    public void closeWindow(UUID windowId)
    {
        TaskManager.sendTaskToNearby(new CloseWindowTask(this.laptop.getPos(), windowId));
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        ListNBT windowsNbt = new ListNBT();
        for (Window window : this.windows)
        {
            CompoundNBT windowNbt = new CompoundNBT();
            {
                CompoundNBT windowStateNbt = new CompoundNBT();
                window.saveState(windowStateNbt);
                windowNbt.put("data", window.serializeNBT());
                windowNbt.put("state", windowStateNbt);
            }
            windowsNbt.add(windowNbt);
        }
        nbt.put("windows", windowsNbt);
        if (focusedWindowId != null)
        {
            nbt.putUniqueId("focusedWindowId", this.focusedWindowId);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background.deserializeNBT(nbt.getCompound("background"));
        this.windows.clear();
        ListNBT windowsNbt = nbt.getList("windows", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < windowsNbt.size(); i++)
        {
            CompoundNBT windowNbt = windowsNbt.getCompound(i);
            Window window = this.createWindow(0, 0);
            window.deserializeNBT(windowNbt.getCompound("data"));
            window.loadState(windowNbt.getCompound("state"));
            this.syncOpenWindow(window);
        }
        this.focusedWindowId = nbt.hasUniqueId("focusedWindowId") ? nbt.getUniqueId("focusedWindowId") : null;
    }

    @Nullable
    @Override
    public Window getWindow(UUID windowId)
    {
        List<Window> windows = this.windows.stream().filter(window -> window.getId().equals(windowId)).collect(Collectors.toList());
        return !windows.isEmpty() ? windows.get(0) : null;
    }

    @Override
    public Window[] getWindows()
    {
        return this.windows.toArray(this.windowsArray);
    }

    @Override
    public int getWindowCount()
    {
        return this.windows.size();
    }

    @Nullable
    @Override
    public UUID getFocusedWindowId()
    {
        return focusedWindowId;
    }

    @Override
    public DesktopBackground getBackground()
    {
        return background;
    }

    // TODO test
    @Override
    public void setBackground(@Nullable DesktopBackground background)
    {
        if (background == null)
            background = DesktopBackground.DEFAULT.copy();

        if (!background.isOnline() && !DesktopManager.isValidLocation(background.getLocation()))
        {
            OpenDevices.LOGGER.warn("Resource Location Desktop Backgrounds need to be registered on both the client and server!");
            return;
        }

        this.background = background;
    }
}
