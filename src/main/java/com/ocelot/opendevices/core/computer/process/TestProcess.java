package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceTrayItems;
import com.ocelot.opendevices.api.application.AppInfo;
import com.ocelot.opendevices.api.application.Application;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TrayItemHandle;
import com.ocelot.opendevices.api.computer.window.WindowHandle;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.util.WindowLayoutManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements Application<Computer>
{
    public static final int TEST_LAYOUT = 0;
    public static final int TEST_LAYOUT2 = 1;

    private boolean dirty;
    private Computer computer;
    private UUID processId;
    private WindowLayoutManager layoutManager;
    private WindowHandle window;
    private WindowHandle window2;
    private TrayItemHandle trayItem;

    public TestProcess(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
        this.layoutManager = new WindowLayoutManager(this.computer, () -> this.dirty = true, () -> new TestProcessLayoutSupplier(this));
        this.window = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);
        this.window2 = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);
        this.trayItem = new TrayItemHandle(this.computer.getTaskBar(), DeviceTrayItems.TEST);
    }

    @Override
    public void init()
    {
        AppInfo info = this.getInfo();

        if (!this.window.exists() && !this.window2.exists())
        {
            if (this.window.create())
            {
                this.window.center();
                this.layoutManager.setCurrentLayout(this.window.getWindowId(), TEST_LAYOUT);
            }
            if (this.window2.create())
            {
                this.window2.center();
                this.layoutManager.setCurrentLayout(this.window2.getWindowId(), TEST_LAYOUT2);
            }
        }

        if (!this.trayItem.exists())
        {
            this.trayItem.create();
        }

        this.window.setTitle(info.getName().getFormattedText() + " v" + info.getVersion());
        this.window.setIcon(info.getIcon());

        this.window2.setTitle("Authors: " + Arrays.toString(Arrays.stream(info.getAuthors()).map(ITextComponent::getFormattedText).toArray(String[]::new)));

        this.dirty = false;
        this.synchronizeClients();
    }

    @Override
    public void update()
    {
        if (this.getDevice().isClient())
        {
            this.layoutManager.update();
        }

        if (this.dirty)
        {
            this.dirty = false;
            this.synchronizeClients();
        }

        if (this.window2.exists() && this.window2.isCloseRequested())
        {
            this.window2.close();
        }
    }

    @Override
    public void onTerminate()
    {
        this.trayItem.remove();
    }

    public WindowHandle getWindow()
    {
        return window;
    }

    public WindowHandle getWindow2()
    {
        return window2;
    }

    public TrayItemHandle getTrayItem()
    {
        return trayItem;
    }

    @Override
    public boolean isTerminated()
    {
        return this.window.isCloseRequested();
    }

    @Override
    public Computer getDevice()
    {
        return computer;
    }

    @Override
    public UUID getProcessId()
    {
        return processId;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layoutManager", this.layoutManager.serializeNBT());
        nbt.put("window", this.window.serializeNBT());
        nbt.put("window2", this.window2.serializeNBT());
        nbt.put("trayItem", this.trayItem.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.layoutManager.deserializeNBT(nbt.getCompound("layoutManager"));
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
        this.trayItem.deserializeNBT(nbt.getCompound("trayItem"));
    }

    @Override
    public CompoundNBT writeSyncNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layoutManager", this.layoutManager.writeSyncNBT());
        nbt.put("window", this.window.serializeNBT());
        nbt.put("window2", this.window2.serializeNBT());
        nbt.put("trayItem", this.trayItem.serializeNBT());
        return nbt;
    }

    @Override
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.layoutManager.readSyncNBT(nbt.getCompound("layoutManager"));
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
        this.trayItem.deserializeNBT(nbt.getCompound("trayItem"));
    }

    @Nullable
    @Override
    public Layout getLayout(UUID windowId)
    {
        return this.layoutManager.getCurrentLayout(windowId);
    }

}