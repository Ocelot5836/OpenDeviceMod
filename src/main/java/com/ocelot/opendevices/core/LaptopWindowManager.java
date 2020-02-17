package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.laptop.window.LaptopWindow;
import com.ocelot.opendevices.core.task.OpenWindowTask;
import com.ocelot.opendevices.core.task.SyncProcessTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Stack;
import java.util.UUID;

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
        for (LaptopWindow window : this.windows)
        {
            window.update();
        }
    }

    public boolean syncOpenWindow(LaptopWindow window)
    {
        if (this.laptop.getProcess(window.getProcessId()) == null)
        {
            OpenDevices.LOGGER.warn("Could not open window under process with id '" + window.getProcessId() + "' as it does not exist. Skipping!");
            return false;
        }

        this.windows.push(window);
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
            if (this.syncOpenWindow(window))
            {
                TaskManager.sendToServer(new OpenWindowTask(this.laptop.getPos(), window.serializeNBT()), TaskManager.TaskReceiver.NEARBY);
            }
        }
        else
        {
            TaskManager.sendToTracking(new OpenWindowTask(this.laptop.getPos(), window.serializeNBT()), this.laptop.getWorld(), this.laptop.getPos());
        }
        return window.getId();
    }

    @Override
    public void focusWindow(@Nullable UUID windowId)
    {

    }

    @Override
    public void closeWindows(Collection<UUID> windowIds)
    {
        this.closeWindows(windowIds.toArray(new UUID[0]));
    }

    @Override
    public void closeWindows(UUID... windowIds)
    {

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
            this.windows.push(this.createWindow(windowsNbt.getCompound(i)));
        }
        this.focusedWindowId = nbt.hasUniqueId("focusedWindowId") ? nbt.getUniqueId("focusedWindowId") : null;
    }
}
