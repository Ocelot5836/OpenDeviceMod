package com.ocelot.opendevices.core.laptop;

import com.google.common.collect.HashBiMap;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.window.Application;
import com.ocelot.opendevices.api.laptop.window.WindowContent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApplicationManager
{
    private static final Type AUTO_REGISTRY = Type.getType(Application.Register.class);
    private static final HashBiMap<ResourceLocation, Class<? extends Application>> REGISTRY = HashBiMap.create();
    private static boolean initialized = false;

    private ApplicationManager() {}

    @SuppressWarnings("unchecked")
    public static void init()
    {
        if (initialized)
        {
            OpenDevices.LOGGER.warn("Attempted to initialize Application Manager even though it has already been initialized. This should NOT happen!");
            return;
        }

        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(AUTO_REGISTRY)).collect(Collectors.toList());

        for (ModFileScanData.AnnotationData data : annotations)
        {
            ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

            String className = data.getClassType().getClassName();
            try
            {
                Class<?> clazz = Class.forName(className);

                if ("minecraft".equals(registryName.getNamespace()) || registryName.getPath().isEmpty())
                    throw new IllegalArgumentException("Application: " + clazz + " does not have a valid registry name. Skipping!");

                if (!Application.class.isAssignableFrom(clazz))
                    throw new IllegalArgumentException("Application: " + clazz + " does not extend Application. Skipping!");

                if (REGISTRY.containsKey(registryName))
                    throw new RuntimeException("Application: " + registryName + " attempted to override existing application. Skipping!");

                REGISTRY.put(registryName, (Class<? extends Application>) clazz);
                OpenDevices.LOGGER.info("Registered application: " + registryName);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not register application class " + className + ". Skipping!", e);
            }
        }

        initialized = true;
    }

    @Nullable
    public static Application createApplication(ResourceLocation registryName)
    {
        if (!REGISTRY.containsKey(registryName))
        {
            throw new RuntimeException("Unregistered Application: " + registryName + ". Use WindowContent#Register annotations to register an application.");
        }

        try
        {
            return Objects.requireNonNull(REGISTRY.get(registryName)).newInstance();
        }
        catch (Exception e)
        {
            OpenDevices.LOGGER.error("Could not create application: " + registryName + ". Verify there is a public empty constructor.", e);
        }

        return null;
    }

    public static ResourceLocation getRegistryName(Class<? extends WindowContent> clazz)
    {
        if (!REGISTRY.containsValue(clazz))
        {
            throw new RuntimeException("Unregistered Application: " + clazz.getName() + ". Use WindowContent#Register annotations to register an application.");
        }

        return REGISTRY.inverse().get(clazz);
    }

    public static boolean isValidApplication(Class<? extends WindowContent> clazz)
    {
        return REGISTRY.inverse().containsKey(clazz);
    }

    public static boolean isValidApplication(ResourceLocation registryName)
    {
        return REGISTRY.containsKey(registryName);
    }
}
