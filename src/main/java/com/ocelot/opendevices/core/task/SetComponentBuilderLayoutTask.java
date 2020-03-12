package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
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
    private ComponentBuilderBoardLayout layout;

    public SetComponentBuilderLayoutTask()
    {
        this(0, null);
    }

    public SetComponentBuilderLayoutTask(int windowId, ComponentBuilderBoardLayout layout)
    {
        this.windowId = windowId;
        this.layout = layout;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putInt("windowId", this.windowId);
        nbt.putString("layout", String.valueOf(DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.getKey(this.layout)));
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.windowId = nbt.getInt("windowId");
        this.layout = DeviceRegistries.COMPONENT_BUILDER_BOARD_LAYOUTS.getValue(new ResourceLocation(nbt.getString("layout")));

        if (player.openContainer instanceof ComponentBuilderContainer && player.openContainer.windowId == this.windowId)
        {
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
