package com.ocelot.opendevices.core.laptop.application;

import com.ocelot.opendevices.api.computer.application.AppInfo;

public class ApplicationInfo implements AppInfo
{
    private String name;
    private String description;
    private String[] authors;
    private String version;
    private boolean translate;

    public ApplicationInfo(String name, String description, String[] authors, String version, boolean translate)
    {
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.translate = translate;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String[] getAuthors()
    {
        return authors;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public boolean shouldTranslate()
    {
        return translate;
    }
}
