package com.ocelot.opendevices;

import com.mrcrayfish.filters.Filters;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.device.ProcessInputRegistry;
import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.desktop.DesktopManager;
import com.ocelot.opendevices.api.computer.settings.LaptopSetting;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.laptop.process.TestProcess;
import com.ocelot.opendevices.core.laptop.process.TestProcessInputHandler;
import com.ocelot.opendevices.core.laptop.process.TestProcessRenderer;
import com.ocelot.opendevices.core.registry.ApplicationRegistryEntry;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import com.ocelot.opendevices.core.registry.TaskRegistryEntry;
import com.ocelot.opendevices.core.render.LaptopTileEntityRenderer;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceItems;
import com.ocelot.opendevices.init.DeviceMessages;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
@SuppressWarnings("unused")
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
            return new ItemStack(DeviceBlocks.RED_LAPTOP);
        }
    };

    private static final Set<ModFileScanData.AnnotationData> annotationScanData = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).collect(Collectors.toSet());

    public OpenDevices()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(FMLCommonSetupEvent event)
    {
        DeviceMessages.init();
        DesktopManager.registerBackgroundLocation(DeviceConstants.DEFAULT_BACKGROUND_LOCATION);
    }

    private void initClient(FMLClientSetupEvent event)
    {
        ProcessInputRegistry.bindInputHandler(TestProcess.class, new TestProcessInputHandler());
        ProcessInputRegistry.bindWindowRenderer(TestProcess.class, new TestProcessRenderer());

        if (ModList.get().isLoaded("filters"))
        {
            Filters.get().register(TAB, new ResourceLocation(MOD_ID, "devices/laptops"), new ItemStack(DeviceBlocks.RED_LAPTOP));
        }
    }

    @SubscribeEvent
    public void onWorldClose(WorldEvent.Unload event)
    {
        if (event.getWorld().isRemote())
        {
            LaptopTileEntityRenderer.INSTANCE.delete();
        }
    }

    @Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
            DistExecutor.runWhenOn(Dist.CLIENT, () -> DeviceBlocks::initClient);
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
        {
            event.getRegistry().registerAll(DeviceBlocks.getTileEntities());
        }

        @SubscribeEvent
        public static void registerSettings(RegistryEvent.Register<LaptopSetting<?>> event)
        {
            Set<ModFileScanData.AnnotationData> annotations = OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.FIELD && it.getAnnotationType().equals(Type.getType(LaptopSetting.Register.class))).collect(Collectors.toSet());

            for (ModFileScanData.AnnotationData data : annotations)
            {
                ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

                String className = data.getClassType().getClassName();
                String fieldName = data.getMemberName();
                try
                {
                    Class<?> clazz = Class.forName(className);
                    Field field = clazz.getField(fieldName);
                    LaptopSetting<?> setting = (LaptopSetting<?>) field.get(null);

                    if (registryName.getPath().isEmpty())
                        throw new IllegalArgumentException("Setting: " + clazz + " does not have a valid registry name. Skipping!");

                    event.getRegistry().register(setting.setRegistryName(registryName));
                }
                catch (Exception e)
                {
                    OpenDevices.LOGGER.error("Could not register setting field " + fieldName + " in " + className + ". Skipping!", e);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerTasks(RegistryEvent.Register<TaskRegistryEntry> event)
        {
            Set<ModFileScanData.AnnotationData> annotations = OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(Type.getType(Task.Register.class))).collect(Collectors.toSet());

            for (ModFileScanData.AnnotationData data : annotations)
            {
                ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

                String className = data.getClassType().getClassName();
                try
                {
                    Class<?> clazz = Class.forName(className);

                    if (registryName.getPath().isEmpty())
                        throw new IllegalArgumentException("Task: " + clazz + " does not have a valid registry name. Skipping!");

                    if (!Task.class.isAssignableFrom(clazz))
                        throw new IllegalArgumentException("Task: " + clazz + " does not extend Task. Skipping!");

                    event.getRegistry().register(new TaskRegistryEntry((Class<? extends Task>) clazz).setRegistryName(registryName));
                }
                catch (Exception e)
                {
                    OpenDevices.LOGGER.error("Could not register task class " + className + ". Skipping!", e);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerProcesses(RegistryEvent.Register<DeviceProcessRegistryEntry> event)
        {
            Set<ModFileScanData.AnnotationData> annotations = OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(Type.getType(DeviceProcess.Register.class))).collect(Collectors.toSet());

            for (ModFileScanData.AnnotationData data : annotations)
            {
                ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

                String className = data.getClassType().getClassName();
                try
                {
                    Class<?> clazz = Class.forName(className);

                    if (registryName.getPath().isEmpty())
                        throw new IllegalArgumentException("Process: " + clazz + " does not have a valid registry name. Skipping!");

                    if (!DeviceProcess.class.isAssignableFrom(clazz))
                        throw new IllegalArgumentException("Process: " + clazz + " does not implement DeviceProcess. Skipping!");

                    event.getRegistry().register(new DeviceProcessRegistryEntry((Class<? extends DeviceProcess<?>>) clazz).setRegistryName(registryName));
                }
                catch (Exception e)
                {
                    OpenDevices.LOGGER.error("Could not register process class " + className + ". Skipping!", e);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerApplications(RegistryEvent.Register<ApplicationRegistryEntry> event)
        {
            Map<String, ModFileScanData.AnnotationData> processClasses = new HashMap<>();
            Set<ModFileScanData.AnnotationData> annotations = OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(Type.getType(Application.Register.class))).collect(Collectors.toSet());
            OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(Type.getType(DeviceProcess.Register.class))).collect(Collectors.toSet()).forEach(data -> processClasses.put(data.getClassType().getClassName(), data));

            for (ModFileScanData.AnnotationData data : annotations)
            {
                String className = data.getClassType().getClassName();

                if (!processClasses.containsKey(className))
                    throw new IllegalArgumentException("Application: " + className + " does not implement DeviceProcess. Skipping!");

                ResourceLocation registryName = new ResourceLocation((String) processClasses.get(className).getAnnotationData().get("value"));

                try
                {
                    Class<?> clazz = Class.forName(className);

                    if (!Application.class.isAssignableFrom(clazz))
                        throw new IllegalArgumentException("Application: " + clazz + " does not implement Application. Skipping!");

                    event.getRegistry().register(new ApplicationRegistryEntry((Class<? extends Application>) clazz).setRegistryName(registryName));
                }
                catch (Exception e)
                {
                    OpenDevices.LOGGER.error("Could not register application class " + className + ". Skipping!", e);
                }
            }
        }
    }
}
