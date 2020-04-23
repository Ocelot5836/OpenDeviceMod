package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.crafting.ComponentBuilderLayout;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.crafting.ClientComponentBuilderLayoutManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

@Task.Register(OpenDevices.MOD_ID + ":sync_component_builder_layouts")
public class SyncComponentBuilderLayoutsTask extends Task
{
    private CompoundNBT data;

    public SyncComponentBuilderLayoutsTask()
    {
    }

    public SyncComponentBuilderLayoutsTask(Map<ResourceLocation, ComponentBuilderLayout> layouts)
    {
        this.data = new CompoundNBT();
        layouts.forEach((resourceLocation, componentBuilderLayout) -> this.data.put(resourceLocation.toString(), componentBuilderLayout.serializeNBT()));
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.put("data", this.data);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.data = nbt.getCompound("data");

        Map<ResourceLocation, ComponentBuilderLayout> layouts = new HashMap<>();
        for (String key : this.data.keySet())
            layouts.put(new ResourceLocation(key), new ComponentBuilderLayout(this.data.getCompound(key)));

        if (world.isRemote())
        {
            ClientComponentBuilderLayoutManager.INSTANCE.receiveComponentBuilderLayouts(layouts);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
    }
}
