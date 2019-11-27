package com.ocelot.opendevices.api.laptop.application;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Loads all registered applications and their classes for both logistical sides.</p>
 *
 * @author Ocelot
 */
public class ApplicationLoader
{
    public static IForgeRegistry<ApplicationRegistryEntry> REGISTRY = null;
    static final Map<ResourceLocation, String> FOUND = new HashMap<>();

    private static final Type AUTO_REGISTRY = Type.getType(Application.Register.class);
    private static boolean initialized = false;

    private ApplicationLoader() {}

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void init()
    {
        if (initialized)
        {
            OpenDevices.LOGGER.warn("Attempted to initialize Application Manager even though it has already been initialized. This should NOT happen!");
        }

        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(it -> it.getTargetType() == ElementType.TYPE && it.getAnnotationType().equals(AUTO_REGISTRY)).collect(Collectors.toList());

        for (ModFileScanData.AnnotationData data : annotations)
        {
            ResourceLocation registryName = new ResourceLocation((String) data.getAnnotationData().get("value"));

            String className = data.getClassType().getClassName();
            try
            {
                if ("minecraft".equals(registryName.getNamespace()) || registryName.getPath().isEmpty())
                    throw new IllegalArgumentException("Application: " + registryName + " does not have a valid registry name. Skipping!");

                if (FOUND.containsKey(registryName))
                    throw new RuntimeException("Application: " + registryName + " attempted to override existing application. Skipping!");

                FOUND.put(registryName, className);
                OpenDevices.LOGGER.debug("Registered application: " + registryName);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not register application class " + className + ". Skipping!", e);
            }
        }

        initialized = true;
    }

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void registerRegistry()
    {
        REGISTRY = new RegistryBuilder<ApplicationRegistryEntry>().setName(new ResourceLocation(OpenDevices.MOD_ID, "applications")).setType(ApplicationRegistryEntry.class).create();
        FOUND.forEach((registryName, className) -> REGISTRY.register(new ApplicationRegistryEntry(className).setRegistryName(registryName)));
        FOUND.clear();
    }

    /**
     * <p>Used as a wrapper for {@link Application}s.</p>
     *
     * @author Ocelot
     */
    public static class ApplicationRegistryEntry extends ForgeRegistryEntry<ApplicationRegistryEntry>
    {
        private String className;

        private ApplicationRegistryEntry(String className)
        {
            this.className = className;
        }

        /**
         * @return The name of the application class
         */
        public String getClassName()
        {
            return className;
        }
    }
}
