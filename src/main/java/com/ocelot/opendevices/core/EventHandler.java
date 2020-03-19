package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.devicemanager.DeviceManagerSavedData;
import com.ocelot.opendevices.core.task.SyncDevicesTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID)
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerJoinedServerEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();
        if (!world.isRemote() && player instanceof ServerPlayerEntity)
        {
            TaskManager.sendToClient(new SyncDevicesTask(((DeviceManagerSavedData) DeviceManager.get(world)).saveDevices()), (ServerPlayerEntity) player, false);
        }
    }
}
