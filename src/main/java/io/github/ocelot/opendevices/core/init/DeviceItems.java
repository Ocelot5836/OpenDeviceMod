package io.github.ocelot.opendevices.core.init;

import io.github.ocelot.opendevices.OpenDevices;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * <p>All default items in the device mod.</p>
 *
 * @author Ocelot
 */
public class DeviceItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OpenDevices.MOD_ID);
}
