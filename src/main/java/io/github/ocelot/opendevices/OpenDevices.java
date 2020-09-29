package io.github.ocelot.opendevices;

import io.github.ocelot.opendevices.core.init.DeviceMessages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Ocelot
 */
@Mod(OpenDevices.MOD_ID)
public class OpenDevices
{
    public static final String MOD_ID = "opendevices";

    public OpenDevices()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::init);
        modBus.addListener(this::initClient);
    }

    private void init(FMLCommonSetupEvent event)
    {
        DeviceMessages.init();
    }

    private void initClient(FMLClientSetupEvent event)
    {
    }
}
