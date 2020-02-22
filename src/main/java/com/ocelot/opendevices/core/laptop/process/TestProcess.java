package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.window.WindowHandle;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements DeviceProcess<Laptop>
{
    private Laptop laptop;
    private UUID processId;
    private WindowHandle window;
    private int a;

    public TestProcess(Laptop laptop, UUID processId)
    {
        this.laptop = laptop;
        this.processId = processId;
        this.window = new WindowHandle(this.laptop, this.processId);
    }

    @Override
    public void init()
    {
        this.window.get();
    }

    @Override
    public void update()
    {
    }

    @Override
    public boolean isTerminated()
    {
        return false;
    }

    @Override
    public Laptop getDevice()
    {
        return laptop;
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
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.window.deserializeNBT(nbt.getCompound("window"));
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