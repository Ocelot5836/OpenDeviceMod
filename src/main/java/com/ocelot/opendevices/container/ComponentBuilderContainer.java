package com.ocelot.opendevices.container;

import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;

public class ComponentBuilderContainer extends Container
{
    private IWorldPosCallable posCallable;

    public ComponentBuilderContainer(int id, IInventory playerInventory)
    {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public ComponentBuilderContainer(int id, IInventory playerInventory, IWorldPosCallable posCallable)
    {
        super(DeviceContainers.COMPONENT_BUILDER, id);
        this.posCallable = posCallable;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return isWithinUsableDistance(posCallable, player, DeviceBlocks.COMPONENT_BUILDER);
    }
}
