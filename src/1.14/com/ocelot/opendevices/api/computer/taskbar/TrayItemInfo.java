package com.ocelot.opendevices.api.computer.taskbar;

import com.ocelot.opendevices.api.computer.application.ApplicationManager;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.core.computer.TrayItemInfoImpl;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>Contains information about each tray item. Tray icon info be queried using {@link ApplicationManager#getAppInfo(Class)} or {@link ApplicationManager#getAppInfo(ResourceLocation)}</p>
 *
 * @author Ocelot
 * @see TrayItem
 * @see ApplicationManager
 */
public interface TrayItemInfo
{
    /**
     * Tray Icon info that is used when there is an error fetching info
     */
    TrayItemInfo EMPTY = new TrayItemInfoImpl(new StringTextComponent("Missing Name"), null).setClickListener(computer -> true);

    /**
     * @return The title of the icon in the tray
     */
    ITextComponent getName();

    /**
     * @return The location of the icon in the tray or null for no custom icon
     */
    @Nullable
    ResourceLocation getIcon();

    /**
     * @return The method called when this tray item is called
     */
    Function<Computer, Boolean> getClickListener();
}
