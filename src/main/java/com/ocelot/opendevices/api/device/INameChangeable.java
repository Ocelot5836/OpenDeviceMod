package com.ocelot.opendevices.api.device;

import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;

public interface INameChangeable extends INameable
{
    void setCustomName(ITextComponent name);
}
