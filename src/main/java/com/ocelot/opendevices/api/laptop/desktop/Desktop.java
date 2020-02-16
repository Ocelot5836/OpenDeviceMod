package com.ocelot.opendevices.api.laptop.desktop;

import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Represents the most abstract form of a desktop for the {@link Laptop}. Manages the {@link DesktopBackground}, desktop icons, and {@link Window}s.</p>
 *
 * @author Ocelot
 * @see Laptop
 */
public interface Desktop
{
//    /**
//     * Opens the application with the specified information and no initialization data.
//     *
//     * @param registryName The registry name of the app
//     */
//    default void openApplication(ResourceLocation registryName)
//    {
//        this.openApplication(registryName, null);
//    }
//
//    /**
//     * Opens the application with the specified information and initialization data.
//     *
//     * @param registryName The registry name of the app
//     * @param initData     Additional data that will be processed on initialization
//     */
//    void openApplication(ResourceLocation registryName, @Nullable CompoundNBT initData);

//    /**
//     * Marks the window with the specified ID as changed and syncs its data.
//     *
//     * @param windowId The id of the window to mark
//     */
//    void markDirty(UUID windowId);

    /**
     * @return The current desktop background
     */
    DesktopBackground getBackground();

    /**
     * Sets the current desktop background.
     *
     * @param background The new background to use
     */
    void setBackground(@Nullable DesktopBackground background);
}
