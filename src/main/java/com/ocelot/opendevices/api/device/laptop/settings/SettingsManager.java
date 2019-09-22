package com.ocelot.opendevices.api.device.laptop.settings;

import com.ocelot.opendevices.api.device.laptop.Laptop;
import com.ocelot.opendevices.tileentity.LaptopTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager
{
    private static final Map<ResourceLocation, LaptopSettingFactory<?>> REGISTRY = new HashMap<>();

    /**
     * Writes the specified option into the laptop system settings if it is registered on the server.
     *
     * @param laptop       The laptop to write the data into
     * @param registryName The registry name of the serializer to use
     * @param value        The value to write under the specified registry name
     * @param <T>          The type of data being written
     */
    public static <T> void write(Laptop laptop, LaptopSettingFactory<T> factory, ResourceLocation registryName, T value)
    {
        if (!laptop.getWorld().isRemote())
        {
            if (isRegistered(factory, registryName))
            {

            }
        }
    }

    public static boolean isRegistered(LaptopSettingFactory<?> factory, ResourceLocation registryName)
    {
        return REGISTRY.containsKey(registryName) && factory.getClass().isInstance(REGISTRY.get(registryName));
    }
}
