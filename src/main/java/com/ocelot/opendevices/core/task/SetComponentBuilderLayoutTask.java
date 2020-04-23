package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.crafting.ComponentBuilderLayout;
import com.ocelot.opendevices.crafting.ComponentBuilderLayoutManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.container.ComponentBuilderContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Task.Register(OpenDevices.MOD_ID + ":set_component_builder_layout")
public class SetComponentBuilderLayoutTask extends Task
{
    private int windowId;
    private ResourceLocation layout;

    public SetComponentBuilderLayoutTask()
    {
        this(-1, ComponentBuilderLayout.EMPTY_LOCATION);
    }

    public SetComponentBuilderLayoutTask(int windowId, ResourceLocation layout)
    {
        this.windowId = windowId;
        this.layout = layout;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putInt("windowId", this.windowId);
        nbt.putString("layout", String.valueOf(this.layout));
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.windowId = nbt.getInt("windowId");
        this.layout = new ResourceLocation(nbt.getString("layout"));

        if (player.openContainer instanceof ComponentBuilderContainer && player.openContainer.windowId == this.windowId)
        {
            if (!ComponentBuilderLayoutManager.get(world).exists(this.layout))
                this.layout = ComponentBuilderLayout.EMPTY_LOCATION;
            ((ComponentBuilderContainer) player.openContainer).setLayout(this.layout);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt)
    {
        if (this.isSucessful())
        {
            this.prepareRequest(nbt);
        }
    }

    @Override
    public void processResponse(CompoundNBT nbt, World world, PlayerEntity player)
    {
        if (this.isSucessful())
        {
            this.processRequest(nbt, world, player);
        }
    }
}
