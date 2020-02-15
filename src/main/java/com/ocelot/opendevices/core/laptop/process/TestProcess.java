package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Laptop;

import java.util.UUID;

public class TestProcess implements DeviceProcess<Laptop>
{
    private UUID processId;

    public TestProcess(UUID processId)
    {
        this.processId = processId;
    }

    @Override
    public void update(Laptop laptop)
    {
        System.out.println("[" + this.processId + "] Hi");
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
}
