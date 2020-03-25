package com.ocelot.opendevices.core.computer.taskbar;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.api.application.AppInfo;
import com.ocelot.opendevices.api.application.ApplicationManager;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.TaskbarIcon;
import com.ocelot.opendevices.api.computer.TaskbarIconType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nullable;

public class ApplicationTaskbarIcon implements TaskbarIcon
{
    private Computer computer;
    private ResourceLocation applicationId;

    private String name;
    private ResourceLocation iconLocation;

    public ApplicationTaskbarIcon(Computer computer)
    {
        this.computer = computer;
    }

    public ApplicationTaskbarIcon(Computer computer, ResourceLocation applicationId)
    {
        this(computer);
        this.applicationId = applicationId;
    }

    @Override
    public void execute()
    {
        if (!this.computer.supportsProcesses())
        {
            OpenDevices.LOGGER.warn("Attempted to execute process from taskbar icon for device '" + this.computer.getClass() + "' that does not support processes.");
            return;
        }

        if (this.computer.executeProcess(this.applicationId) != null)
        {
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            // TODO send notification that the process failed to execute
        }
    }

    @Override
    public String getName()
    {
        if (this.name == null)
        {
            AppInfo info = ApplicationManager.getAppInfo(this.applicationId);
            this.name = info != null ? info.getName().getFormattedText() : String.valueOf(MissingTextureSprite.getLocation());
        }
        return this.name;
    }

    @Nullable
    @Override
    public ResourceLocation getIconSprite()
    {
        if (this.iconLocation == null)
        {
            AppInfo info = ApplicationManager.getAppInfo(this.applicationId);
            this.iconLocation = IconManager.getWindowIcon(info.getIcon()).getName();
        }
        return iconLocation;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public TaskbarIconType getType()
    {
        return TaskbarIconType.APPLICATION;
    }

    public ResourceLocation getApplicationId()
    {
        return applicationId;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("applicationId", this.applicationId.toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.applicationId = new ResourceLocation(nbt.getString("applicationId"));
        this.name = null;
        this.iconLocation = null;
    }
}
