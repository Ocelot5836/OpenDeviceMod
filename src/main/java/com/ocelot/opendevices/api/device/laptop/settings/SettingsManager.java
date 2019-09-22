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
 * <p>Manages all the setting handling for the {@link Laptop}.<p/>
 * <p>To read to settings use {@link Laptop#readSetting(LaptopSetting)}. To write to settings use {@link Laptop#writeSetting(LaptopSetting, Object)}.</p>
 *
 * @author Ocelot
 * @see Laptop
 * @see LaptopSetting
 */
public class SettingsManager
{
    private static final Type AUTO_REGISTRY = Type.getType(SettingsManager.Register.class);
    private static final Map<ResourceLocation, LaptopSetting<?>> REGISTRY = new HashMap<>();
    private static boolean initialized = false;

    /**
     * This should never be used by the consumer. Core use only!
     */
    public static void init()
    {
        if (initialized)
        {
            OpenDevices.LOGGER.warn("Attempted to initialize Settings Manager even though it has already been initialized. This should NOT happen!");
            return;
        }

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
                    throw new RuntimeException("Setting: " + registryName + " attempted to override existing setting. Skipping!");

                REGISTRY.put(registryName, setting);
                OpenDevices.LOGGER.warn("Registered setting: " + registryName);
            }
            catch (Exception e)
            {
                OpenDevices.LOGGER.error("Could not register setting field " + fieldName + " in " + className + ". Skipping!", e);
            }
        }

        initialized = true;
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
