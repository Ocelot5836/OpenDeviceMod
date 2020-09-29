package com.ocelot.opendevices.api.computer.taskbar;

import com.ocelot.opendevices.api.device.process.DeviceProcess;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>Manages tray icon creation and management automatically for a {@link DeviceProcess}.</p>
 *
 * @author Ocelot
 * @see DeviceProcess
 * @see TaskBar
 */
public class TrayItemHandle implements INBTSerializable<CompoundNBT>
{
    private TaskBar taskBar;
    private ResourceLocation registryName;
    private UUID trayItemId;

    public TrayItemHandle(TaskBar taskBar, ResourceLocation registryName)
    {
        this.taskBar = taskBar;
        this.registryName = registryName;
    }

    /**
     * Creates a new tray item if there is not one.
     *
     * @return Whether or not a new tray item was actually created
     */
    public boolean create()
    {
        TrayItem trayItem = this.taskBar.getTrayItem(this.trayItemId);

        if (trayItem == null)
        {
            this.trayItemId = null;
        }
        else
        {
            return false;
        }

        this.trayItemId = this.taskBar.createTrayIcon(this.registryName);
        return true;
    }

    /**
     * Closes this tray item if there is one.
     */
    public void remove()
    {
        if (this.trayItemId != null)
        {
            this.taskBar.removeTrayItem(this.trayItemId);
            this.trayItemId = null;
        }
    }

    /**
     * @return The id of this tray item or null if there is no tray item
     */
    @Nullable
    public UUID getTrayItemId()
    {
        return trayItemId;
    }

    /**
     * @return Whether or not this tray item exists
     */
    public boolean exists()
    {
        if (this.trayItemId == null)
            return false;
        return this.taskBar.getTrayItem(this.trayItemId) != null;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        if (this.trayItemId != null)
        {
            nbt.putUniqueId("trayItemId", this.trayItemId);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.trayItemId = nbt.hasUniqueId("trayItemId") ? nbt.getUniqueId("trayItemId") : null;
    }
}
