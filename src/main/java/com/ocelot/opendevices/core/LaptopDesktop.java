package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;

    LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
    }

    //    private void openWindow(LaptopWindow window)
    //    {
    //        if (this.laptop.isClient())
    //        {
    //            if (!(window instanceof WindowClient))
    //            {
    //                OpenDevices.LOGGER.error("Attempted to open server window on the client!");
    //                return;
    //            }
    //        }
    //        else
    //        {
    //            if (window instanceof WindowClient)
    //            {
    //                OpenDevices.LOGGER.error("Attempted to open client window on the server!");
    //                return;
    //            }
    //        }
    //
    //        if (this.windows.stream().noneMatch(frame -> frame.equals(window)))
    //        {
    //            this.laptop.execute(() ->
    //            {
    //                window.init();
    //                this.windows.push(window);
    //                this.laptop.getTaskBar().addWindow(window);
    //                window.focus();
    //            });
    //        }
    //    }

    public void update()
    {
    }

    //    public void syncOpenApplication(LaptopWindowOld window, CompoundNBT contentData)
    //    {
    //        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
    //        {
    //            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
    //            return;
    //        }
    //
    //        if (contentData != null)
    //        {
    //            window.setStateData(contentData);
    //        }
    //        else
    //        {
    //            window.create();
    //        }
    //        //        this.openWindow(window);
    //    }
    //
    //    public void syncFocusWindow(@Nullable UUID windowId)
    //    {
    //        if (this.focusedWindowId != null && this.focusedWindowId.equals(windowId))
    //            return;
    //
    //        LaptopWindowOld lastFocusWindow = this.getWindow(this.focusedWindowId);
    //        LaptopWindowOld window = this.getWindow(windowId);
    //
    //        this.laptop.execute(() ->
    //        {
    //            if (lastFocusWindow != null)
    //            {
    //                lastFocusWindow.onLostFocus();
    //            }
    //
    //            if (window != null)
    //            {
    //                this.windows.remove(window);
    //                this.windows.push(window);
    //                window.onGainFocus();
    //            }
    //
    //            this.focusedWindowId = windowId == null ? null : window != null ? windowId : null;
    //        });
    //    }
    //
    //    public void syncCloseWindow(UUID windowId)
    //    {
    //        LaptopWindowOld window = this.getWindow(windowId);
    //        if (window != null)
    //        {
    //            this.laptop.execute(() ->
    //            {
    //                window.onClose();
    //                this.windows.remove(window);
    //                this.laptop.getTaskBar().removeWindow(window);
    //                if (this.focusedWindowId != null && this.focusedWindowId.equals(window.getId()))
    //                {
    //                    this.focusWindow(null);
    //                }
    //            });
    //        }
    //    }
    //
    //    public void syncApplication(LaptopWindowOld window, CompoundNBT contentData)
    //    {
    //        this.laptop.execute(() -> window.setStateData(contentData));
    //    }
    //
    //    @Override
    //    public void openApplication(ResourceLocation registryName, @Nullable CompoundNBT initData)
    //    {
    //        if (this.windows.size() >= DeviceConstants.MAX_OPEN_APPS)
    //        {
    //            this.windows.setSize(DeviceConstants.MAX_OPEN_APPS);
    //            return;
    //        }
    //
    //        if (!WindowContentType.APPLICATION.isValid(registryName))
    //        {
    //            OpenDevices.LOGGER.warn("Attempted to open invalid application: '" + registryName + "'! Applications MUST be registered on both the client AND server to function!");
    //            return;
    //        }
    //
    //        LaptopWindowOld window = this.createWindow(registryName, initData);
    //        if (this.laptop.isClient())
    //        {
    //            this.syncOpenApplication(window, null);
    //            TaskManager.sendToServer(new OpenApplicationTask(this.laptop.getPos(), window.serializeNBT(), window.getStateData()), TaskManager.TaskReceiver.NEARBY);
    //        }
    //        else
    //        {
    //            TaskManager.sendToTracking(new OpenApplicationTask(this.laptop.getPos(), window.serializeNBT(), null), this.laptop.getWorld(), this.laptop.getPos());
    //        }
    //    }

    //    @Override
    //    public void markDirty(UUID windowId)
    //    {
    //        LaptopWindow window = this.getWindow(windowId);
    //        if (window == null)
    //        {
    //            OpenDevices.LOGGER.error("Attempted to sync window that doesn't exist!");
    //            return;
    //        }
    //
    //        CompoundNBT contentData = window.getStateData();
    //        if (this.laptop.isClient())
    //        {
    //            TaskManager.sendToServer(new SyncWindowTask(this.laptop.getPos(), windowId, contentData), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
    //        }
    //        else
    //        {
    //            this.laptop.markDirty();
    //        }
    //    }

    //    @Override
    //    public void focusWindow(@Nullable UUID windowId)
    //    {
    //        if (this.focusedWindowId == windowId)
    //            return;
    //
    //        if (this.laptop.isClient())
    //        {
    //            this.syncFocusWindow(windowId);
    //            TaskManager.sendToServer(new FocusWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
    //        }
    //        else
    //        {
    //            TaskManager.sendToTracking(new FocusWindowTask(this.laptop.getPos(), windowId), this.laptop.getWorld(), this.laptop.getPos());
    //        }
    //    }
    //
    //    @Override
    //    public void closeAllWindows()
    //    {
    //        UUID[] windowIds = this.windows.stream().map(LaptopWindowOld::getId).distinct().toArray(UUID[]::new);
    //        if (this.laptop.isClient())
    //        {
    //            for (UUID windowId : windowIds)
    //            {
    //                this.syncCloseWindow(windowId);
    //            }
    //            TaskManager.sendToServer(new CloseWindowTask(this.laptop.getPos(), windowIds), TaskManager.TaskReceiver.NEARBY);
    //        }
    //        else
    //        {
    //            TaskManager.sendToTracking(new CloseWindowTask(this.laptop.getPos(), windowIds), this.laptop.getWorld(), this.laptop.getPos());
    //        }
    //    }
    //
    //    @Override
    //    public void closeWindow(UUID windowId)
    //    {
    //        if (this.laptop.isClient())
    //        {
    //            this.syncCloseWindow(windowId);
    //            TaskManager.sendToServer(new CloseWindowTask(this.laptop.getPos(), windowId), TaskManager.TaskReceiver.NEARBY);
    //        }
    //        else
    //        {
    //            TaskManager.sendToTracking(new CloseWindowTask(this.laptop.getPos(), windowId), this.laptop.getWorld(), this.laptop.getPos());
    //        }
    //    }

    @Override
    public DesktopBackground getBackground()
    {
        return background;
    }

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

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background.deserializeNBT(nbt.getCompound("background"));
    }
}
