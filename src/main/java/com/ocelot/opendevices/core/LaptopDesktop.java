package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import com.ocelot.opendevices.api.laptop.window.application.Application;
import com.ocelot.opendevices.api.laptop.window.application.ApplicationManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import com.ocelot.opendevices.core.laptop.window.WindowClient;
import com.ocelot.opendevices.core.task.CloseWindowTask;
import com.ocelot.opendevices.core.task.FocusWindowTask;
import com.ocelot.opendevices.core.task.OpenWindowTask;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
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
    private Stack<LaptopWindow> windows;
    private LaptopWindow[] windowsArray;
    private UUID focusedWindowId;

    public LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
        this.windowsArray = new LaptopWindow[DeviceConstants.MAX_OPEN_APPS];
        this.focusedWindowId = null;
    }

    public LaptopWindow createWindow(WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        return DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop, contentType, contentId, width, height), () -> () -> new LaptopWindow(this.laptop, contentType, contentId, width, height));
    }

    public LaptopWindow createWindow(CompoundNBT dataNBT, CompoundNBT stateNBT)
    {
        LaptopWindow window = DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop), () -> () -> new LaptopWindow(this.laptop));
        window.deserializeNBT(dataNBT);
        window.loadState(stateNBT);
        return window;
    }

    public void update()
    {
        for (LaptopWindow window : this.windows)
        {
            window.update();
        }
    }

    public void syncOpenWindow(LaptopWindow window)
    {
        if (this.windows.stream().noneMatch(frame -> frame.equals(window)))
        {
            this.laptop.execute(() ->
            {
                this.windows.push(window);
                window.focus();
            });
        }
    }

    public void syncFocusWindow(@Nullable UUID windowId)
    {
        if (this.focusedWindowId == windowId)
            return;

        LaptopWindow lastFocusWindow = this.getWindow(this.focusedWindowId);
        LaptopWindow window = this.getWindow(windowId);

        this.laptop.execute(() ->
        {
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
        });
    }

    public void syncCloseWindow(UUID windowId)
    {
        LaptopWindow window = this.getWindow(windowId);
        if (window != null)
        {
            this.laptop.execute(() ->
            {
                window.onClose();
                this.windows.remove(window);
                if (this.focusedWindowId != null && this.focusedWindowId.equals(window.getId()))
                {
                    this.focusWindow(null);
                }
            });
        }
    }

    @Override
    public void openApplication(ResourceLocation registryName)
    {
        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
        {
            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
            return;
        }

        TaskManager.sendTask(new OpenWindowTask(this.laptop.getPos(), createWindow(WindowContentType.APPLICATION, registryName, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT)), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
    }

    @Override
    public void closeApplication(ResourceLocation registryName)
    {

    }

    @Override
    public void openApplication(Class<? extends Application> clazz)
    {
        openApplication(ApplicationManager.getRegistryName(clazz));
    }

    @Override
    public void closeApplication(Class<? extends Application> clazz)
    {
        closeApplication(ApplicationManager.getRegistryName(clazz));
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {
        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTask(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
            this.syncFocusWindow(windowId);
        }
        else
        {
            TaskManager.sendTask(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
        }
    }

    @Override
    public void closeAllWindows()
    {
        this.laptop.execute(() -> this.windows.forEach(window -> this.closeWindow(window.getId())));
    }

    @Override
    public void closeWindow(UUID windowId)
    {
        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTask(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
            this.syncCloseWindow(windowId);
        }
        else
        {
            TaskManager.sendTask(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        ListNBT windowsNbt = new ListNBT();
        for (LaptopWindow window : this.windows)
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
        if (this.focusedWindowId != null)
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
            this.windows.push(this.createWindow(windowNbt.getCompound("data"), windowNbt.getCompound("state")));
        }
        this.focusedWindowId = nbt.hasUniqueId("focusedWindowId") ? nbt.getUniqueId("focusedWindowId") : null;
    }

    @Nullable
    @Override
    public LaptopWindow getWindow(UUID windowId)
    {
        if (windowId == null)
            return null;
        List<LaptopWindow> windows = this.windows.stream().filter(window -> window.getId().equals(windowId)).collect(Collectors.toList());
        return !windows.isEmpty() ? windows.get(0) : null;
    }

    @Override
    public LaptopWindow[] getWindows()
    {
        return this.windows.toArray(this.windowsArray);
    }

    /**
     * @return The currently focused window or null if there is no window focused
     */
    @Nullable
    public LaptopWindow getFocusedWindow()
    {
        return this.getFocusedWindowId() == null ? null : this.getWindow(this.getFocusedWindowId());
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
