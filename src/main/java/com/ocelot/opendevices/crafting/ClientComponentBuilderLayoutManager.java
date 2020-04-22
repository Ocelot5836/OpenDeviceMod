package com.ocelot.opendevices.crafting;

import com.ocelot.opendevices.api.DeviceResourceTypes;
import com.ocelot.opendevices.api.crafting.ComponentBuilderLayout;
import com.ocelot.opendevices.api.crafting.ComponentBuilderLayoutManager;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.resource.VanillaResourceType;

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
        RenderUtil.refreshResources(Minecraft.getInstance(), DeviceResourceTypes.MAIN_ATLAS);
    }

    @Override
    public boolean exists(ResourceLocation registryName)
    {
        return this.layouts.containsKey(registryName);
    }

    @Override
    public ComponentBuilderLayout getLayout(ResourceLocation registryName)
    {
        return this.layouts.get(registryName);
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return this.layouts.keySet();
    }
}
