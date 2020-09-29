package com.ocelot.opendevices.core.computer.taskbar;

import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.taskbar.TaskbarIcon;
import com.ocelot.opendevices.api.computer.taskbar.TaskbarIconType;
import com.ocelot.opendevices.api.computer.window.Window;
import com.ocelot.opendevices.api.computer.window.WindowManager;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public class WindowTaskbarIcon implements TaskbarIcon
{
    private WindowManager windowManager;
    private UUID windowId;

    public WindowTaskbarIcon(Computer computer)
    {
        this.windowManager = computer.getWindowManager();
    }

    public WindowTaskbarIcon(Computer computer, UUID windowId)
    {
        this(computer);
        this.windowId = windowId;
    }

    @Override
    public boolean execute()
    {
        // TODO make window visible or invisible
        this.windowManager.focusWindow(this.windowId);
        return true;
    }

    @Override
    public String getName()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window != null ? window.getTitle() : String.valueOf(MissingTextureSprite.getLocation());
    }

    @Nullable
    @Override
    public ResourceLocation getIconSprite()
    {
        Window window = this.windowManager.getWindow(this.windowId);
        return window != null ? window.getIconSprite() : null;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public TaskbarIconType getType()
    {
        return TaskbarIconType.WINDOW;
    }

    public UUID getWindowId()
    {
        return windowId;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("windowId", this.windowId);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.windowId = nbt.getUniqueId("windowId");
    }
}
