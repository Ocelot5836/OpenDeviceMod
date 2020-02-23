package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Computer;
import com.ocelot.opendevices.api.laptop.window.WindowHandle;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements DeviceProcess<Computer>
{
    private Computer computer;
    private UUID processId;
    private WindowHandle window;
    private WindowHandle window2;

    public TestProcess(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
        this.window = new WindowHandle(this.computer, this.processId);
        this.window2 = new WindowHandle(this.computer, this.processId);
    }

    @Override
    public void init()
    {
        this.window.get();
        this.window.center();
        this.window2.get();
        this.window2.center();
    }

    @Override
    public void update()
    {
        if (this.window2.exists() && this.window2.isCloseRequested())
        {
            this.computer.getWindowManager().closeWindows(this.window2.get());
        }
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
}