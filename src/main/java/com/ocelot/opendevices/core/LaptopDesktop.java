package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.Constants;
import com.ocelot.opendevices.api.laptop.desktop.Desktop;
import com.ocelot.opendevices.api.laptop.desktop.DesktopBackground;
import com.ocelot.opendevices.api.laptop.desktop.DesktopManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.CloseWindowTask;
import com.ocelot.opendevices.core.task.OpenWindowTask;
import com.ocelot.opendevices.core.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

public class LaptopDesktop implements Desktop, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private DesktopBackground background;
    private Stack<Window> windows;
    private Window[] windowsArray;

    public LaptopDesktop(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
        this.background = DesktopBackground.DEFAULT.copy();
        this.windows = new Stack<>();
        this.windowsArray = new Window[Constants.MAX_OPEN_APPS];
    }

    protected Window createNewWindow(float x, float y, int width, int height)
    {
        return new Window(x, y, width, height);
    }

    protected Window createNewWindow(int width, int height)
    {
        return new Window(width, height);
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
        if (this.windows.size() >= Constants.MAX_OPEN_APPS)
        {
            this.windows.setSize(Constants.MAX_OPEN_APPS);
            return;
        }

        Window window = this.createNewWindow(200, 100);
        this.openWindow(window);
        TaskManager.sendTaskToNearby(new OpenWindowTask(this.laptop.getPos(), window));
    }

    public void openWindow(Window window)
    {
        if (this.windows.stream().noneMatch(frame -> frame.equals(window)))
        {
            this.laptop.execute(() -> this.windows.push(window));
        }
    }

    @Override
    public void closeAllWindows()
    {
        this.laptop.execute(() -> this.windows.forEach(this::closeWindow));
    }

    @Override
    public void closeWindow(UUID windowId)
    {
        TaskManager.sendTaskToNearby(new CloseWindowTask(this.laptop.getPos(), windowId));
    }

    public void syncCloseWindow(Window window)
    {
        window.onClose();
        this.windows.remove(window);
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

    @Nullable
    @Override
    public Window getWindow(UUID windowId)
    {
        List<Window> windows = this.windows.stream().filter(window -> window.getId().equals(windowId)).collect(Collectors.toList());
        return !windows.isEmpty() ? windows.get(0) : null;
    }

    @Override
    public Window[] getWindows()
    {
        return this.windows.toArray(this.windowsArray);
    }

    @Override
    public DesktopBackground getBackground()
    {
        return background;
    }

    public Stack<Window> getWindowStack()
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
