package com.ocelot.opendevices.core;

import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.taskbar.TaskBar;
import com.ocelot.opendevices.api.laptop.window.Window;
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
    private List<Window> openedWindows;

    LaptopTaskBar(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.openedWindows = new ArrayList<>();
    }

    void addWindow(Window window)
    {
        this.openedWindows.add(window);
    }

    void removeWindow(Window window)
    {
        this.openedWindows.remove(window);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT windowOrderNBT = new ListNBT();
        this.openedWindows.forEach(window ->
        {
            if (window == null)
                return;
            CompoundNBT windowNBT = new CompoundNBT();
            windowNBT.putUniqueId("id", window.getId());
            windowOrderNBT.add(windowNBT);
        });
        nbt.put("windowOrder", windowOrderNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.openedWindows.clear();

        ListNBT windowOrderNBT = nbt.getList("windowOrder", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < windowOrderNBT.size(); i++)
        {
            UUID id = windowOrderNBT.getCompound(i).getUniqueId("id");
            Window window = this.laptop.getDesktop().getWindow(id);
            if (window != null)
            {
                this.openedWindows.add(window);
            }
        }

        this.openedWindows.addAll(Arrays.stream(this.laptop.getDesktop().getWindows()).filter(window -> !this.openedWindows.contains(window)).collect(Collectors.toList()));
    }

    @Override
    public boolean isEnlarged()
    {
        return this.laptop.readSetting(LaptopSettings.TASKBAR_ENLARGED);
    }

    @Override
    public Window[] getDisplayedWindows()
    {
        return this.openedWindows.toArray(new Window[0]);
    }
}
