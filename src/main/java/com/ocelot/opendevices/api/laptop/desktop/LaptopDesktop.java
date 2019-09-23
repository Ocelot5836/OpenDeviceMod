package com.ocelot.opendevices.api.laptop.desktop;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Represents the desktop for the {@link Laptop}. Manages the {@link LaptopDesktopBackground}, desktop icons, and applications.</p>
 *
 * @author Ocelot
 * @see Laptop
 */
public class LaptopDesktop implements INBTSerializable<CompoundNBT>
{
    private static final Set<ResourceLocation> VALID_BACKGROUND_LOCATIONS = new HashSet<>();

    private Laptop laptop;
    private LaptopDesktopBackground background;

    public LaptopDesktop(Laptop laptop)
    {
        this.laptop = laptop;
        this.background = LaptopDesktopBackground.DEFAULT.copy();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background.deserializeNBT(nbt.getCompound("background"));
    }

    public LaptopDesktopBackground getBackground()
    {
        return background;
    }

    // TODO test
    public void setDesktopBackground(@Nullable LaptopDesktopBackground background)
    {
        if (background == null)
            background = LaptopDesktopBackground.DEFAULT.copy();

        if (!background.isOnline() && !isValidLocation(background.getLocation()))
        {
            OpenDevices.LOGGER.warn("Resource Location Desktop Backgrounds need to be registered on both the client and server!");
            return;
        }

        this.background = background;
    }

    /**
     * All valid background locations must be registered on both the server and the client.
     *
     * @param location The location of the background to register
     */
    public static void registerBackgroundLocation(ResourceLocation location)
    {
        if (location != null)
        {
            OpenDevices.LOGGER.info("Registered Desktop Background location: " + location);
            VALID_BACKGROUND_LOCATIONS.add(location);
        }
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
