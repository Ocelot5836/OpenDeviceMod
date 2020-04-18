package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.computer.taskbar.TaskBar;
import com.ocelot.opendevices.api.computer.taskbar.TaskbarIcon;
import com.ocelot.opendevices.api.computer.taskbar.TaskbarIconType;
import com.ocelot.opendevices.api.computer.taskbar.TrayItem;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.computer.taskbar.ApplicationTaskbarIcon;
import com.ocelot.opendevices.core.computer.taskbar.LaptopTrayItem;
import com.ocelot.opendevices.core.computer.taskbar.WindowTaskbarIcon;
import com.ocelot.opendevices.core.task.SyncTrayIconsTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class LaptopTaskBar implements TaskBar, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private List<TaskbarIcon> icons;
    private List<TrayItem> trayItems;

    public LaptopTaskBar(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.icons = new ArrayList<>();
        this.trayItems = new ArrayList<>();
        this.addAllApplicationsDebug();
    }

    private void addAllApplicationsDebug()
    {
        DeviceRegistries.APPLICATIONS.getKeys().forEach(applicationId -> this.icons.add(new ApplicationTaskbarIcon(this.laptop, applicationId)));
    }

    private void syncTrayIcons()
    {
        if (this.laptop.isClient())
        {
            TaskManager.sendToServer(new SyncTrayIconsTask(this.laptop.getAddress(), this.serializeTrayItems()), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SyncTrayIconsTask(this.laptop.getAddress(), this.serializeTrayItems()), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    void addWindow(Window window)
    {
        this.icons.add(new WindowTaskbarIcon(this.laptop, window.getId()));
    }

    void removeWindow(Window window)
    {
        this.icons.removeIf(icon -> icon instanceof WindowTaskbarIcon && ((WindowTaskbarIcon) icon).getWindowId().equals(window.getId()));
    }

    @Override
    public UUID createTrayIcon(ResourceLocation icon)
    {
        if (!DeviceRegistries.TRAY_ITEMS.containsKey(icon))
            throw new IllegalArgumentException("Could not add tray icon " + icon + " as it is not registered!");
        UUID id = UUID.randomUUID();
        this.trayItems.add(new LaptopTrayItem(id, icon));
        this.syncTrayIcons();
        return id;
    }

    @Override
    public void removeTrayItem(UUID id)
    {
        this.trayItems.removeIf(trayIcon -> trayIcon.getId().equals(id));
        this.syncTrayIcons();
    }

    @Override
    public TaskbarIcon[] getDisplayedIcons()
    {
        return this.icons.toArray(new TaskbarIcon[0]);
    }

    @Override
    public TrayItem[] getTrayItems()
    {
        return this.trayItems.toArray(new TrayItem[0]);
    }

    @Nullable
    @Override
    public TrayItem getTrayItem(UUID id)
    {
        return this.trayItems.stream().filter(trayIcon -> trayIcon.getId().equals(id)).findAny().orElse(null);
    }

    public ListNBT serializeTrayItems()
    {
        ListNBT nbt = new ListNBT();
        this.trayItems.forEach(trayIcon ->
        {
            if (trayIcon == null || !DeviceRegistries.TRAY_ITEMS.containsKey(trayIcon.getRegistryName()))
                return;
            CompoundNBT trayIconNbt = new CompoundNBT();
            trayIconNbt.putString("registryName", trayIcon.getRegistryName().toString());
            trayIconNbt.putUniqueId("id", trayIcon.getId());
            nbt.add(trayIconNbt);
        });
        return nbt;
    }

    public void deserializeTrayItems(ListNBT nbt)
    {
        this.trayItems.clear();
        for (int i = 0; i < nbt.size(); i++)
        {
            CompoundNBT trayIconNbt = nbt.getCompound(i);
            ResourceLocation trayIconName = new ResourceLocation(trayIconNbt.getString("registryName"));
            if (!DeviceRegistries.TRAY_ITEMS.containsKey(trayIconName))
            {
                OpenDevices.LOGGER.warn("Could not deserialize tray icon '" + trayIconName + "' as it doesn't exist!");
                continue;
            }
            this.trayItems.add(new LaptopTrayItem(trayIconNbt.getUniqueId("id"), trayIconName));
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT iconsNbt = new ListNBT();
        this.icons.forEach(icon ->
        {
            if (icon == null)
                return;
            if (icon.getType() == TaskbarIconType.APPLICATION)
                return; // TODO this is for debug purposes
            CompoundNBT iconNbt = new CompoundNBT();
            iconNbt.putByte("type", icon.getType().serialize());
            iconNbt.put("data", icon.serializeNBT());
            iconsNbt.add(iconNbt);
        });
        nbt.put("icons", iconsNbt);

        nbt.put("trayIcons", this.serializeTrayItems());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.icons.clear();
        this.addAllApplicationsDebug();

        ListNBT windowOrderNBT = nbt.getList("icons", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < windowOrderNBT.size(); i++)
        {
            CompoundNBT iconNbt = windowOrderNBT.getCompound(i);
            TaskbarIcon icon = TaskbarIconType.deserialize(iconNbt.getByte("type")).apply(this.laptop);
            icon.deserializeNBT(iconNbt.getCompound("data"));
            this.icons.add(icon);
        }

        this.deserializeTrayItems(nbt.getList("trayIcons", Constants.NBT.TAG_COMPOUND));

        List<UUID> windowIds = Arrays.stream(this.laptop.getWindowManager().getWindows()).map(Window::getId).collect(Collectors.toList());
        windowIds.removeAll(this.icons.stream().filter(taskbarIcon -> taskbarIcon instanceof WindowTaskbarIcon).map(taskbarIcon -> ((WindowTaskbarIcon) taskbarIcon).getWindowId()).collect(Collectors.toSet()));
        windowIds.forEach(windowId ->
        {
            Window window = this.laptop.getWindowManager().getWindow(windowId);
            if (window != null)
            {
                this.addWindow(window);
            }
        });
    }
}
