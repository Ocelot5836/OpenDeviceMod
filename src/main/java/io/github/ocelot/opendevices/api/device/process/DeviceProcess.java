package io.github.ocelot.opendevices.api.device.process;

import io.github.ocelot.opendevices.api.device.Device;
import net.minecraft.nbt.CompoundNBT;

public interface DeviceProcess<T extends Device>
{
    void tick();

    void write(CompoundNBT nbt);

    void read(CompoundNBT nbt);

    DeviceProcessSerializer<T> getProcessType();
}
