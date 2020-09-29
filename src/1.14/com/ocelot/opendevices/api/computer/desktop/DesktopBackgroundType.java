package com.ocelot.opendevices.api.computer.desktop;

import com.ocelot.opendevices.core.computer.desktop.LaptopLocalDesktopBackground;
import com.ocelot.opendevices.core.computer.desktop.LaptopOnlineDesktopBackground;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>Defines the types of {@link DesktopBackground} that exist.</p>
 *
 * @author Ocelot
 */
public enum DesktopBackgroundType implements Function<CompoundNBT, DesktopBackground>
{
    RESOURCE_LOCATION("resource_location", LaptopLocalDesktopBackground::new), ONLINE("online", LaptopOnlineDesktopBackground::new);

    private static final Map<String, DesktopBackgroundType> NAME_LOOKUP = new HashMap<>();

    private String registryName;
    private Function<CompoundNBT, DesktopBackground> creator;

    DesktopBackgroundType(String registryName, Function<CompoundNBT, DesktopBackground> creator)
    {
        this.registryName = registryName;
        this.creator = creator;
    }

    /**
     * @return The registry name of this background type
     */
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public DesktopBackground apply(CompoundNBT nbt)
    {
        return this.creator.apply(nbt);
    }

    /**
     * Fetches a desktop background type from name.
     *
     * @param registryName The registry name of the background type
     * @return The desktop background type with that registry name or {@link #RESOURCE_LOCATION} if there was an error
     */
    public static DesktopBackgroundType byName(String registryName)
    {
        return NAME_LOOKUP.getOrDefault(registryName.toLowerCase(Locale.ROOT), RESOURCE_LOCATION);
    }

    static
    {
        for (DesktopBackgroundType type : DesktopBackgroundType.values())
        {
            NAME_LOOKUP.put(type.getRegistryName().toLowerCase(Locale.ROOT), type);
        }
    }
}
