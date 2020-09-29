package com.ocelot.opendevices.core.devicemanager;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientDeviceManager implements DeviceManager
{
    @OnlyIn(Dist.CLIENT)
    public static final ClientDeviceManager INSTANCE = new ClientDeviceManager();

    private Map<UUID, Device> devices;

    public ClientDeviceManager()
    {
        this.devices = new HashMap<>();
    }

    @Override
    public <T extends Device> void add(T device, DeviceSerializer<T> serializer)
    {
        throw new UnsupportedOperationException("Devices cannot be added client side");
    }

    @Override
    public void remove(UUID address)
    {
        throw new UnsupportedOperationException("Devices cannot be removed client side");
    }

    @Nullable
    @Override
    public Device locate(UUID address)
    {
        if (!this.exists(address))
        {
            OpenDevices.LOGGER.warn("Could not locate device with address '" + address + "' as it does not exist.");
            return null;
        }

        return this.devices.get(address);
    }

    @Override
    public boolean exists(UUID address)
    {
        return this.devices.containsKey(address);
    }

    public void receiveDevices(ListNBT nbt)
    {
        Map<UUID, Pair<ResourceLocation, CompoundNBT>> devices = new HashMap<>();
        DeviceManagerSavedData.loadDevices(devices, nbt);
        this.devices.clear();
        devices.forEach((address, pair) ->
        {
            Device device = DeviceManagerSavedData.parseDevice(Minecraft.getInstance().world, address, pair.getLeft(), pair.getRight());
            if (device != null)
            {
                this.devices.put(address, device);
            }
        });
    }
}
