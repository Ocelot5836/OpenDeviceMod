package com.ocelot.opendevices;

import com.mrcrayfish.filters.Filters;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.api.application.Application;
import com.ocelot.opendevices.api.application.ApplicationManager;
import com.ocelot.opendevices.api.computer.settings.LaptopSetting;
import com.ocelot.opendevices.api.device.ApplicationInputHandler;
import com.ocelot.opendevices.api.device.ApplicationWindowRenderer;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.device.process.ProcessInputRegistry;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import com.ocelot.opendevices.api.registry.DeviceCircuitBoardItem;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.api.util.OnlineImageCache;
import com.ocelot.opendevices.core.EventHandler;
import com.ocelot.opendevices.core.computer.process.TestProcess;
import com.ocelot.opendevices.core.registry.*;
import com.ocelot.opendevices.core.render.LaptopTileEntityRenderer;
import com.ocelot.opendevices.init.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
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
import net.minecraftforge.registries.ForgeRegistries;
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
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        DistExecutor.runWhenOn(Dist.CLIENT, () -> IconManager::init);
    }

    private void init(FMLCommonSetupEvent event)
    {
        DeviceMessages.init();
    }

    private void initClient(FMLClientSetupEvent event)
    {
        ProcessInputRegistry.bindInputHandler(TestProcess.class, new ApplicationInputHandler<>());
        ProcessInputRegistry.bindWindowRenderer(TestProcess.class, new ApplicationWindowRenderer<>());

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
            OnlineImageCache.clear();
        }
    }

    @Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
        {
            event.getRegistry().registerAll(DeviceRecipes.getRecipeSerializers());
        }

        @SubscribeEvent
        public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event)
        {
            event.getRegistry().registerAll(DeviceContainers.getContainerTypes());
            DistExecutor.runWhenOn(Dist.CLIENT, () -> DeviceScreens::register);
        }

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
        public static void registerBoardLayouts(RegistryEvent.Register<ComponentBuilderBoardLayout> event)
        {
            event.getRegistry().registerAll(DeviceBoardLayouts.getBoardLayouts());
        }

        @SubscribeEvent
        public static void registerBoardTextures(RegistryEvent.Register<ComponentBuilderBoardTexture> event)
        {
            for (Item item : ForgeRegistries.ITEMS)
            {
                if (item instanceof DeviceCircuitBoardItem)
                {
                    ResourceLocation registryName = item.getRegistryName();
                    assert registryName != null;
                    event.getRegistry().register(new ComponentBuilderBoardTexture(item, ((DeviceCircuitBoardItem) item).getTextureLocation(item)).setRegistryName(registryName));
                }
            }
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

        @SubscribeEvent
        public static void registerDeviceSerializers(RegistryEvent.Register<DeviceSerializer<?>> event)
        {
            Set<ModFileScanData.AnnotationData> annotations = OpenDevices.annotationScanData.stream().filter(it -> it.getTargetType() == ElementType.FIELD && it.getAnnotationType().equals(Type.getType(DeviceSerializer.Register.class))).collect(Collectors.toSet());

            for (ModFileScanData.AnnotationData data : annotations)
            {
                ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

                String className = data.getClassType().getClassName();
                String fieldName = data.getMemberName();
                try
                {
                    Class<?> clazz = Class.forName(className);
                    Field field = clazz.getField(fieldName);
                    DeviceSerializer<?> serializer = (DeviceSerializer<?>) field.get(null);

                    if (registryName.getPath().isEmpty())
                        throw new IllegalArgumentException("Device Serializer: " + clazz + " does not have a valid registry name. Skipping!");

                    event.getRegistry().register(serializer.setRegistryName(registryName));
                }
                catch (Exception e)
                {
                    OpenDevices.LOGGER.error("Could not register device serializer field " + fieldName + " in " + className + ". Skipping!", e);
                }
            }
        }

        @SubscribeEvent
        public static void registerWindowIcons(RegistryEvent.Register<WindowIconRegistryEntry> event)
        {
            event.getRegistry().registerAll(DeviceRegistries.APPLICATIONS.getKeys().stream().map(registryName -> new WindowIconRegistryEntry(ApplicationManager.getAppInfo(registryName).getIcon()).setRegistryName(registryName)).toArray(WindowIconRegistryEntry[]::new));
        }
    }
}
