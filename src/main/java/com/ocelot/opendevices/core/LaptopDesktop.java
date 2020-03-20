package com.ocelot.opendevices.core;

import com.ocelot.opendevices.api.DeviceDesktopBackgrounds;
import com.ocelot.opendevices.api.computer.desktop.Desktop;
import com.ocelot.opendevices.api.computer.desktop.DesktopBackground;
import com.ocelot.opendevices.api.computer.desktop.DesktopBackgroundType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private DesktopBackground background;

    public LaptopDesktop()
    {
        this.background = DeviceDesktopBackgrounds.DEFAULT.get();
    }

    public void update()
    {
        this.background.update();
    }

    @Override
    public DesktopBackground getBackground()
    {
        return background;
    }

    @Override
    public void setBackground(@Nullable DesktopBackground background)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("backgroundType", this.background.getType().getRegistryName());
        nbt.put("background", this.background.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background = DesktopBackgroundType.byName(nbt.getString("backgroundType")).apply(nbt.getCompound("background"));
    }
}
