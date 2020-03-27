package com.ocelot.opendevices.core;

import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.computer.TaskBar;
import com.ocelot.opendevices.api.computer.TaskbarIcon;
import com.ocelot.opendevices.api.computer.TaskbarIconType;
import com.ocelot.opendevices.api.computer.TrayIcon;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.core.computer.taskbar.ApplicationTaskbarIcon;
import com.ocelot.opendevices.core.computer.taskbar.WindowTaskbarIcon;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LaptopTaskBar implements TaskBar, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private List<TaskbarIcon> icons;

    public LaptopTaskBar(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.icons = new ArrayList<>();
        this.addAllApplicationsDebug();
    }

    private void addAllApplicationsDebug()
    {
        DeviceRegistries.APPLICATIONS.getKeys().forEach(applicationId -> this.icons.add(new ApplicationTaskbarIcon(this.laptop, applicationId)));
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
    public boolean isEnlarged()
    {
        return this.laptop.readSetting(LaptopSettings.TASKBAR_ENLARGED);
    }

    @Override
    public TaskbarIcon[] getDisplayedIcons()
    {
        return this.icons.toArray(new TaskbarIcon[0]);
    }

    @Override
    public TrayIcon[] getTrayIcons()
    {
        return new TrayIcon[0];
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT windowOrderNBT = new ListNBT();
        this.icons.forEach(icon ->
        {
            if (icon == null)
                return;
            if (icon.getType() == TaskbarIconType.APPLICATION)
                return; // TODO this is for debug purposes
            CompoundNBT iconNbt = new CompoundNBT();
            iconNbt.putByte("type", icon.getType().serialize());
            iconNbt.put("data", icon.serializeNBT());
            windowOrderNBT.add(iconNbt);
        });
        nbt.put("windowOrder", windowOrderNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.icons.clear();
        this.addAllApplicationsDebug();

        ListNBT windowOrderNBT = nbt.getList("windowOrder", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < windowOrderNBT.size(); i++)
        {
            CompoundNBT iconNbt = windowOrderNBT.getCompound(i);
            TaskbarIcon icon = TaskbarIconType.deserialize(iconNbt.getByte("type")).apply(this.laptop);
            icon.deserializeNBT(iconNbt.getCompound("data"));
            this.icons.add(icon);
        }

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
