package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public class TestProcess implements DeviceProcess<Laptop>
{
    private UUID processId;
    private int a;

    public TestProcess(UUID processId)
    {
        this.processId = processId;
    }

    public TestProcess(UUID processId, CompoundNBT nbt)
    {
        this(processId);
        this.read(nbt);
    }

    private void read(CompoundNBT nbt)
    {
        this.a = nbt.getInt("a");
    }

    @Override
    public void update(Laptop laptop)
    {
        if (!laptop.isClient())
        {
            this.a++;
            laptop.syncProcess(this.processId);
        }
        System.out.println(this.a);
    }

    @Override
    public boolean isTerminated()
    {
        return false;
    }

    @Override
    public UUID getProcessId()
    {
        return processId;
    }

    @Override
    public CompoundNBT save()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("a", this.a);
        return nbt;
    }

    @Override
    public CompoundNBT writeSyncNBT()
    {
        return this.save();
    }

    @Override
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.read(nbt);
    }
}