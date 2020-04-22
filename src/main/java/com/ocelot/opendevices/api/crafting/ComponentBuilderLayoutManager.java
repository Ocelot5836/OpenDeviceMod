package com.ocelot.opendevices.api.crafting;

import com.ocelot.opendevices.crafting.ClientComponentBuilderLayoutManager;
import com.ocelot.opendevices.crafting.ComponentBuilderLayoutLoader;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import java.util.Set;

/**
 * <p>Manages component builder layouts.</p>
 *
 * @author Ocelot
 */
public interface ComponentBuilderLayoutManager
{
    /**
     * Checks to see if a component builder layout with the specified registry name exists.
     *
     * @param registryName The name to check
     * @return Whether or not that layout exists
     */
    boolean exists(ResourceLocation registryName);

    /**
     * Gets the component builder layout with the specified registry name or null if it doesn't exist.
     *
     * @param registryName The name to check
     * @return The component builder layout or null if there is no layout with that id
     */
    @Nullable
    ComponentBuilderLayout getLayout(ResourceLocation registryName);

    /**
     * @return A set containing all keys to component builder layoutss
     */
    Set<ResourceLocation> getKeys();

    /**
     * Fetches an instance of the component builder from the server.
     *
     * @param world The world to fetch the data from
     * @return The component builder layout manager for that world
     */
    static ComponentBuilderLayoutManager get(IWorld world)
    {
        if (world.isRemote())
            return ClientComponentBuilderLayoutManager.INSTANCE;
        if (!(world instanceof ServerWorld))
            throw new IllegalStateException("Server side world is not an instance of ServerWorld?");
        return ComponentBuilderLayoutLoader.INSTANCE;
    }
}
