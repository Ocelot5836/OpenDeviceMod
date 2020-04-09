package com.ocelot.opendevices.core.task;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceManager;
import com.ocelot.opendevices.api.task.Task;
import com.ocelot.opendevices.core.LaptopTaskBar;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

@Task.Register(OpenDevices.MOD_ID + ":sync_tray_icons")
public class SyncTrayIconsTask extends Task
{
    private UUID address;
    private ListNBT trayItemsData;

    public SyncTrayIconsTask()
    {
        this(null, null);
    }

    public SyncTrayIconsTask(UUID address, ListNBT trayItemsData)
    {
        this.address = address;
        this.trayItemsData = trayItemsData;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt)
    {
        nbt.putUniqueId("address", this.address);
        nbt.put("trayItemsData", this.trayItemsData);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player)
    {
        this.address = nbt.getUniqueId("address");
        this.trayItemsData = nbt.getList("trayItemsData", Constants.NBT.TAG_COMPOUND);

        Device device = DeviceManager.get(world).locate(this.address);
        if (device instanceof LaptopTileEntity)
        {
            LaptopTileEntity laptop = (LaptopTileEntity) device;
            LaptopTaskBar taskBar = laptop.getTaskBar();
            taskBar.deserializeTrayItems(this.trayItemsData);
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
