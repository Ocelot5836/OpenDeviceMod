package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.DeviceProcess;

public class TestProcess implements DeviceProcess
{
    @Override
    public void update()
    {
        System.out.println("Hi");
    }

    @Override
    public boolean isTerminated()
    {
        return false;
    }
}
