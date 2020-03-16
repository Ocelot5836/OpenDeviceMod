package com.ocelot.opendevices.core.computer.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.application.AppInfo;
import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.window.WindowHandle;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.util.WindowLayoutManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Application.Register
@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements Application<Computer>
{
    public static final int TEST_LAYOUT = 0;
    public static final int TEST_LAYOUT2 = 1;

    private Computer computer;
    private UUID processId;
    private WindowLayoutManager layoutManager;
    private WindowHandle window;
    private WindowHandle window2;

    public TestProcess(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
        this.layoutManager = new WindowLayoutManager(this.computer, this::synchronizeClients, TestProcessLayoutSupplier::new);
        this.window = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);
        this.window2 = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);
    }

    @Override
    public void init()
    {
        AppInfo info = this.getInfo();
        if (this.window.create())
        {
            this.window.center();
            this.window.setTitle(info.getName() + " v" + info.getVersion());
        }

        if (this.window2.create())
        {
            this.window2.center();
            this.window2.setTitle("Authors: " + Arrays.toString(info.getAuthors()));
        }

        this.layoutManager.setCurrentLayout(this.window.getWindowId(), TEST_LAYOUT);
        this.layoutManager.setCurrentLayout(this.window2.getWindowId(), TEST_LAYOUT2);

        this.synchronizeClients();
    }

    @Override
    public void update()
    {
        if (this.getDevice().isClient())
        {
            this.layoutManager.update();
        }

        if (this.window2.exists() && this.window2.isCloseRequested())
        {
            this.window2.close();
        }
    }

    public WindowHandle getWindow()
    {
        return window;
    }

    public WindowHandle getWindow2()
    {
        return window2;
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
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.layoutManager.deserializeNBT(nbt.getList("layoutManager", Constants.NBT.TAG_COMPOUND));
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
    }

    @Override
    public CompoundNBT writeSyncNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layoutManager", this.layoutManager.writeSyncNBT());
        nbt.put("window", this.window.serializeNBT());
        nbt.put("window2", this.window2.serializeNBT());
        return nbt;
    }

    @Override
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.layoutManager.readSyncNBT(nbt.getCompound("layoutManager"));
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
    }

    @Nullable
    @Override
    public Layout getLayout(UUID windowId)
    {
        return this.layoutManager.getCurrentLayout(windowId);
    }

}