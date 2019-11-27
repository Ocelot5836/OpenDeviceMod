package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.DeviceRegistries;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
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
import java.util.Stack;
import java.util.UUID;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;
    private Stack<LaptopWindow> windows;
    private LaptopWindow[] windowsArray;
    private UUID focusedWindowId;

    LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
        this.windowsArray = new LaptopWindow[DeviceConstants.MAX_OPEN_APPS];
        this.focusedWindowId = null;
    }

    private LaptopWindow createWindow(@Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        return DistExecutor.runForDist(() -> () -> new WindowClient(this.laptop, initData, contentType, contentId, width, height), () -> () -> new LaptopWindow(this.laptop, initData, contentType, contentId, width, height));
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
                this.laptop.getTaskBar().addWindow(window);
                window.focus();
                window.init();
            });
        }
    }

    public void syncFocusWindow(@Nullable UUID windowId)
    {
        if (this.focusedWindowId != null && this.focusedWindowId.equals(windowId))
            return;

        LaptopWindow lastFocusWindow = this.getWindow(this.focusedWindowId);
        LaptopWindow window = this.getWindow(windowId);

        this.laptop.execute(() ->
        {
            if (lastFocusWindow != null)
            {
                lastFocusWindow.onLostFocus();
            }

            if (window != null)
            {
                this.windows.remove(window);
                this.windows.push(window);
                window.onGainFocus();
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
                this.laptop.getTaskBar().removeWindow(window);
                if (this.focusedWindowId != null && this.focusedWindowId.equals(window.getId()))
                {
                    this.focusWindow(null);
                }
            });
        }
    }

    @Override
    public void openApplication(ResourceLocation registryName, @Nullable CompoundNBT initData)
    {
        if (this.laptop.getUser() == null)
            return;

        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
        {
            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
            return;
        }

        if (!DeviceRegistries.APPLICATIONS.containsKey(registryName))
        {
            OpenDevices.LOGGER.warn("Attempted to open invalid application '" + registryName + "' on the server! Applications MUST be registered on both the client AND server to function!");
            return;
        }

        TaskManager.sendTaskToServer(new OpenWindowTask(this.laptop.getPos(), createWindow(initData, WindowContentType.APPLICATION, registryName, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT)), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {
        if (this.laptop.getUser() == null)
            return;

        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTaskToServer(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
            this.syncFocusWindow(windowId);
        }
        else
        {
            TaskManager.sendTaskTo(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
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
        if (this.laptop.getUser() == null)
            return;

        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTaskToServer(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
            this.syncCloseWindow(windowId);
        }
        else
        {
            TaskManager.sendTaskTo(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
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
        return this.windows.stream().filter(window -> window.getId().equals(windowId)).findFirst().orElse(null);
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
