package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.OpenWindowTask;
import com.ocelot.opendevices.core.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Stack;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;
    private Stack<Window> windows;

    public LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
    }

    public void update()
    {
        for (Window window : this.windows)
        {
            window.update();
        }
    }

    @Deprecated
    public void openApplicationTest()
    {
        this.laptop.execute(() ->
        {
            if (this.windows.size() >= Constants.MAX_OPEN_APPS)
            {
                this.windows.setSize(Constants.MAX_OPEN_APPS);
                return;
            }

            Window window = new Window(200, 100);
            this.openWindow(window);
            TaskManager.sendTaskToNearby(new OpenWindowTask(this.laptop.getPos(), window));
        });
    }

    public void openWindow(Window window)
    {
        // TODO test or smth
        this.windows.push(window);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("background", this.background.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.background.deserializeNBT(nbt.getCompound("background"));
    }

    @Override
    public DesktopBackground getBackground()
    {
        return background;
    }

    public Stack<Window> getWindows()
    {
        return windows;
    }

    // TODO test
    @Override
    public void setBackground(@Nullable DesktopBackground background)
    {
        if (background == null)
            background = DesktopBackground.DEFAULT.copy();

        if (!background.isOnline() && !DesktopManager.isValidLocation(background.getLocation()))
        {
            OpenDevices.LOGGER.warn("Resource Location Desktop Backgrounds need to be registered on both the client and server!");
            return;
        }

        this.background = background;
    }
}
