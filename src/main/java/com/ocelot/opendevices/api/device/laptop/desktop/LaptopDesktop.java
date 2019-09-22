package com.ocelot.opendevices.api.device.laptop.desktop;

import com.ocelot.opendevices.api.device.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LaptopDesktop implements INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private LaptopDesktopBackground background;

    public LaptopDesktop(Laptop laptop)
    {
        this.laptop = laptop;
        this.background = LaptopDesktopBackground.createDefault();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background.deserializeNBT(nbt.getCompound("background"));
    }

    public LaptopDesktopBackground getBackground()
    {
        return background;
    }
}
