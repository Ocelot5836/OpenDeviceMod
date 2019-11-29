package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import com.ocelot.opendevices.core.laptop.window.WindowClient;
import com.ocelot.opendevices.core.task.CloseWindowTask;
import com.ocelot.opendevices.core.task.FocusWindowTask;
import com.ocelot.opendevices.core.task.OpenApplicationTask;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Stack;
import java.util.UUID;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;
    private Stack<LaptopWindow> windows;
    private UUID focusedWindowId;

    LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
        this.focusedWindowId = null;
    }

    private LaptopWindow createWindow(ResourceLocation registryName, @Nullable CompoundNBT initData)
    {
        return this.laptop.isClient() ? new WindowClient(this.laptop, initData, WindowContentType.APPLICATION, registryName, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT) : new LaptopWindow(this.laptop, initData, WindowContentType.APPLICATION, registryName, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT);
    }

    public LaptopWindow createWindow(CompoundNBT windowData)
    {
        LaptopWindow window = this.laptop.isClient() ? new WindowClient(this.laptop) : new LaptopWindow(this.laptop);
        window.deserializeNBT(windowData);
        return window;
    }

    private void openWindow(LaptopWindow window)
    {
        if (this.laptop.isClient())
        {
            if (!(window instanceof WindowClient))
            {
                OpenDevices.LOGGER.error("Attempted to open server window on the client!");
                return;
            }
        }
        else
        {
            if (window instanceof WindowClient)
            {
                OpenDevices.LOGGER.error("Attempted to open client window on the server!");
                return;
            }
        }

        if (this.windows.stream().noneMatch(frame -> frame.equals(window)))
        {
            this.laptop.execute(() ->
            {
                window.init();
                this.windows.push(window);
                this.laptop.getTaskBar().addWindow(window);
                window.focus();
            });
        }
    }

    public void update()
    {
        for (LaptopWindow window : this.windows)
        {
            window.update();
        }
    }

    public void syncOpenApplication(LaptopWindow window, CompoundNBT contentData)
    {
        if (contentData != null)
        {
            window.setContentData(contentData);
        }
        else
        {
            window.create();
        }
        this.openWindow(window);
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

    public void syncApplication(LaptopWindow window, CompoundNBT contentData)
    {
        this.laptop.execute(() -> window.setContentData(contentData));
    }

    @Override
    public void openApplication(ResourceLocation registryName, @Nullable CompoundNBT initData)
    {
        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
        {
            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
            return;
        }

        if (!DeviceRegistries.APPLICATIONS.containsKey(registryName))
        {
            OpenDevices.LOGGER.warn("Attempted to open invalid application: '" + registryName + "'! Applications MUST be registered on both the client AND server to function!");
            return;
        }

        LaptopWindow window = this.createWindow(registryName, initData);
        if (this.laptop.isClient())
        {
            this.syncOpenApplication(window, null);
            TaskManager.sendToServer(new OpenApplicationTask(this.laptop.getPos(), window.serializeNBT(), window.getContentData()), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToServer(new OpenApplicationTask(this.laptop.getPos(), window.serializeNBT(), null), TaskManager.TaskReceiver.NEARBY);
        }
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {
        if (this.focusedWindowId == windowId)
            return;

        if (this.laptop.isClient())
        {
            this.syncFocusWindow(windowId);
            TaskManager.sendToServer(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendTo(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
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
        if (this.laptop.isClient())
        {
            this.syncCloseWindow(windowId);
            TaskManager.sendToServer(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendTo(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
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
                windowNbt.put("data", window.serializeNBT());
                windowNbt.put("contentData", window.getContentData());
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
            LaptopWindow window = this.createWindow(windowNbt.getCompound("data"));
            window.setContentData(windowNbt.getCompound("contentData"));
            this.windows.push(window);
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
        return this.windows.toArray(new LaptopWindow[0]);
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
