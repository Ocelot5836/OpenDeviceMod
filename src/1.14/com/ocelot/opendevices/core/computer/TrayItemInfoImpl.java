package com.ocelot.opendevices.core.computer;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TrayItemInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public class TrayItemInfoImpl implements TrayItemInfo
{
    private ITextComponent name;
    private ResourceLocation icon;
    private Function<Computer, Boolean> clickListener;

    public TrayItemInfoImpl(ITextComponent name, ResourceLocation icon)
    {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public ITextComponent getName()
    {
        return name;
    }

    @Nullable
    @Override
    public ResourceLocation getIcon()
    {
        return icon;
    }

    @Override
    public Function<Computer, Boolean> getClickListener()
    {
        return clickListener;
    }

    public TrayItemInfoImpl setClickListener(Function<Computer, Boolean> layoutGenerator)
    {
        this.clickListener = layoutGenerator;
        return this;
    }
}
