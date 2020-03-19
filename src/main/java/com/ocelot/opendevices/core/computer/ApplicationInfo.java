package com.ocelot.opendevices.core.computer;

import com.ocelot.opendevices.api.application.AppInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ApplicationInfo implements AppInfo
{
    private ITextComponent name;
    private ITextComponent description;
    private ITextComponent[] authors;
    private String version;
    private ResourceLocation icon;

    public ApplicationInfo(ITextComponent name, ITextComponent description, ITextComponent[] authors, String version, ResourceLocation icon)
    {
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.icon = icon;
    }

    @Override
    public ITextComponent getName()
    {
        return name;
    }

    @Override
    public ITextComponent getDescription()
    {
        return description;
    }

    @Override
    public ITextComponent[] getAuthors()
    {
        return authors;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public ResourceLocation getIcon()
    {
        return icon;
    }
}
