package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

/**
 * <p>Manages window creation and management automatically for a {@link DeviceProcess}.</p>
 *
 * @author Ocelot
 * @see DeviceProcess
 * @see WindowManager
 */
public class WindowHandle implements INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private UUID processId;
    private UUID windowId;

    public WindowHandle(Laptop laptop, UUID processId)
    {
        this.laptop = laptop;
        this.processId = processId;
    }

    /**
     * @return The id of the window bound to this handle or a new window if there is none
     */
    public UUID get()
    {
        WindowManager windowManager = this.laptop.getWindowManager();
        Window window = windowManager.getWindow(this.windowId);

        if (window == null)
        {
            this.windowId = null;
        }
        else
        {
            this.windowId = window.getId();
        }
        if (this.windowId != null)
        {
            return this.windowId;
        }

        return this.windowId = windowManager.openWindow(this.processId);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        if (this.windowId != null)
        {
            nbt.putUniqueId("windowId", this.windowId);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.windowId = nbt.hasUniqueId("windowId") ? nbt.getUniqueId("windowId") : null;
    }
}
