package com.ocelot.opendevices.api.device.laptop.settings;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages all the setting handling for the {@link Laptop}. To read/write to settings use {@link Laptop#readSetting(LaptopSetting)} and {@link Laptop#writeSetting(LaptopSetting, Object)} respectively.
 */
public class SettingsManager
{
    private static final Type AUTO_REGISTRY = Type.getType(SettingsManager.Register.class);
    private static final Map<ResourceLocation, LaptopSetting<?>> REGISTRY = new HashMap<>();

    /**
     * Search for annotations and register them.
     */
    public static void init()
    {
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(it -> it.getAnnotationType().equals(AUTO_REGISTRY)).collect(Collectors.toList());

        for (ModFileScanData.AnnotationData data : annotations)
        {
            String className = data.getClassType().getClassName();
            String fieldName = data.getMemberName();
            try
            {
                Class clazz = Class.forName(className);
                Field field = clazz.getField(fieldName);
                LaptopSetting<?> setting = (LaptopSetting<?>) field.get(null);
                ResourceLocation registryName = setting.getRegistryName();

                if (REGISTRY.containsKey(registryName))
                    throw new RuntimeException("Setting: " + registryName + " attempted to override existing setting!");

                REGISTRY.put(registryName, setting);
                OpenDevices.LOGGER.warn("Registered setting: " + registryName);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.warn("Could not register setting field " + fieldName + " in " + className + ". Skipping!", e);
            }
        }
    }

    /**
     * Checks the registry for the specified setting under the specified name.
     *
     * @param setting The setting to look for
     * @return Whether or not that registry exists
     */
    public static boolean isRegistered(LaptopSetting<?> setting)
    {
        return REGISTRY.containsKey(setting.getRegistryName()) && setting.getClass().isInstance(REGISTRY.get(setting.getRegistryName()));
    }

    /**
     * Registers a new type of setting for the Laptop.
     */
    public @interface Register {}
}
