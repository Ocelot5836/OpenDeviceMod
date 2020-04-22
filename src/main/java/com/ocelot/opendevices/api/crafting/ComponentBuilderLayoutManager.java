package com.ocelot.opendevices.api.crafting;

import com.ocelot.opendevices.core.ClientComponentBuilderLayoutManager;
import com.ocelot.opendevices.core.ComponentBuilderLayoutLoader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

/**
 * <p>Manages component builder layouts.</p>
 *
 * @author Ocelot
 */
public interface ComponentBuilderLayoutManager
{
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
