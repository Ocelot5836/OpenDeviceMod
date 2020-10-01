package io.github.ocelot.opendevices.core.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.api.device.process.DeviceProcess;
import io.github.ocelot.opendevices.api.device.process.DeviceProcessSerializer;
import io.github.ocelot.opendevices.api.device.process.DeviceProcesses;
import net.minecraft.nbt.CompoundNBT;

public class TestDeviceProcess implements DeviceProcess<Device>
{
    private final Device device;

    public TestDeviceProcess(Device device)
    {
        this.device = device;
    }

    @Override
    public void tick()
    {
    }

    @Override
    public void write(CompoundNBT nbt)
    {
    }

    @Override
    public void read(CompoundNBT nbt)
    {
    }

    @Override
    public DeviceProcessSerializer<Device> getProcessType()
    {
        return DeviceProcesses.TEST;
    }
}
