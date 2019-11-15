package com.ocelot.opendevices.core;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.laptop.taskbar.TaskBar;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.core.laptop.window.WindowClient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LaptopTaskBar implements TaskBar, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private Set<Window> openedWindows;

    LaptopTaskBar(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.openedWindows = new HashSet<>();
    }

    public void update()
    {
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }

    @Nullable
    public Window getWindow(double mouseX, double mouseY)
    {
        int size = this.isEnlarged() ? 16 : 8;
        int i = 0;

        for (Window value : this.openedWindows)
        {
            if (value instanceof WindowClient)
            {
                WindowClient window = (WindowClient) value;
                if (!StringUtils.isEmpty(window.getContent().getTitle()) && RenderUtil.isMouseInside(mouseX, mouseY, 4 + (size + 4) * i, DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.getHeight() + 4, 4 + (size + 4) * i + size, DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.getHeight() + 4 + size))
                {
                    return window;
                }
                i++;
            }
        }

        return null;
    }

    @Override
    public boolean isEnlarged()
    {
        return this.laptop.readSetting(LaptopSettings.TASKBAR_ENLARGED);
    }

    @Override
    public Window[] getOpenedWindows()
    {
        //TODO make the opened windows some list of windows opened in order instead of the stack
        this.openedWindows.clear();
        this.openedWindows.addAll(Arrays.asList(this.laptop.getDesktop().getWindows()));
        return this.openedWindows.toArray(new Window[0]);
    }
}
