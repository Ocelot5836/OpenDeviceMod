package com.ocelot.opendevices.api.util;

import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;

// TODO probably remove this
public interface INameChangeable extends INameable
{
    void setCustomName(ITextComponent name);
}
