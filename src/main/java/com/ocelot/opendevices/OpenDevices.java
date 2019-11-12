package com.ocelot.opendevices;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.core.laptop.SettingsManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.laptop.ApplicationManager;
import com.ocelot.opendevices.core.render.LaptopTileEntityRenderer;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceItems;
import com.ocelot.opendevices.init.DeviceMessages;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ocelot
 */
@Mod(OpenDevices.MOD_ID)
public class OpenDevices
{
    public static final String MOD_ID = "opendevices";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ItemGroup TAB = new ItemGroup(MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(DeviceBlocks.WHITE_LAPTOP);
        }
    };

    public OpenDevices()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(FMLCommonSetupEvent event)
    {
        DeviceMessages.init();
        SettingsManager.init();
        TaskManager.init();
        ApplicationManager.init();

        DesktopManager.registerBackgroundLocation(DeviceConstants.DEFAULT_BACKGROUND_LOCATION);
    }

    private void initClient(FMLClientSetupEvent event)
    {
        DeviceBlocks.initClient();
        MinecraftForge.EVENT_BUS.register(LaptopTileEntityRenderer.INSTANCE);
    }

    @SubscribeEvent
    public void onWorldClose(WorldEvent.Unload event)
    {
        if (event.getWorld().isRemote())
        {
            LaptopTileEntityRenderer.INSTANCE.delete();
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().registerAll(DeviceItems.getItems());
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().registerAll(DeviceBlocks.getBlocks());
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
        {
            event.getRegistry().registerAll(DeviceBlocks.getTileEntities());
        }
    }
}
