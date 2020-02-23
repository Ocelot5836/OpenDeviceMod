package com.ocelot.opendevices.api.laptop.desktop;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Manages all the backgrounds for the {@link Desktop}.</p>
 *
 * @author Ocelot
 * @see Desktop
 * @see DesktopBackground
 * @deprecated TODO update to use {@link IForgeRegistry} so it syncs with the server
 */
public class DesktopManager
{
    private static final Set<ResourceLocation> VALID_BACKGROUND_LOCATIONS = new HashSet<>();

    /**
     * All valid background locations must be registered on both the server and the client.
     *
     * @param location The location of the background to register
     */
    public static void registerBackgroundLocation(ResourceLocation location)
    {
        VALID_BACKGROUND_LOCATIONS.add(Objects.requireNonNull(location, "Desktop Background locations can not be null."));
        OpenDevices.LOGGER.info("Registered Desktop Background location: " + location);
    }

    /**
     * Checks to see if the specified location is valid.
     *
     * @param location The location to check
     * @return Whether or not that location was valid
     */
    public static boolean isValidLocation(ResourceLocation location)
    {
        return VALID_BACKGROUND_LOCATIONS.contains(location);
    }
}
