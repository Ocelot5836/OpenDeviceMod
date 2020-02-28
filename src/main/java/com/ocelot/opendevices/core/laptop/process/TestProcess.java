package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Computer;
import com.ocelot.opendevices.api.laptop.application.AppInfo;
import com.ocelot.opendevices.api.laptop.application.Application;
import com.ocelot.opendevices.api.laptop.window.WindowHandle;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Application.Register
@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements DeviceProcess<Computer>, Application
{
    private Computer computer;
    private UUID processId;
    private WindowHandle window;
    private WindowHandle window2;

    public TestProcess(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
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

        this.synchronizeClients();
    }

    @Override
    public void update()
    {
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
        nbt.put("window", this.window.serializeNBT());
        nbt.put("window2", this.window2.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
    }

    @Override
    public CompoundNBT writeSyncNBT()
    {
        return this.serializeNBT();
    }

    @Override
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    @Nullable
    @Override
    public UUID getLayout(UUID windowId)
    {
        return null;
    }
}