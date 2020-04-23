package com.ocelot.opendevices.crafting;

import com.ocelot.opendevices.api.DeviceResourceTypes;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientComponentBuilderLayoutManager implements ComponentBuilderLayoutManager
{
    public static final ClientComponentBuilderLayoutManager INSTANCE = new ClientComponentBuilderLayoutManager();

    private final Map<ResourceLocation, ComponentBuilderLayout> layouts;

    public ClientComponentBuilderLayoutManager()
    {
        this.layouts = new HashMap<>();
    }

    public void receiveComponentBuilderLayouts(Map<ResourceLocation, ComponentBuilderLayout> layouts)
    {
        this.layouts.clear();
        this.layouts.putAll(layouts);
    }

    @Override
    public boolean exists(ResourceLocation registryName)
    {
        return this.layouts.containsKey(registryName);
    }

    @Override
    public ComponentBuilderLayout getLayout(ResourceLocation registryName)
    {
        return this.layouts.getOrDefault(registryName, ComponentBuilderLayout.EMPTY);
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return this.layouts.keySet();
    }
}
