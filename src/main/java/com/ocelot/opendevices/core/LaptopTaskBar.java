package com.ocelot.opendevices.core;

import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.taskbar.TaskBar;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LaptopTaskBar implements TaskBar, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;

    LaptopTaskBar(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
    }

    public void update()
    {
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }

    @Override
    public boolean isEnlarged()
    {
        return this.laptop.readSetting(LaptopSettings.TASKBAR_ENLARGED);
    }
}
