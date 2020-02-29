package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.component.TextComponent;
import com.ocelot.opendevices.api.component.WindowLayoutManager;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.application.AppInfo;
import com.ocelot.opendevices.api.computer.application.Application;
import com.ocelot.opendevices.api.computer.window.WindowHandle;
import com.ocelot.opendevices.api.device.DeviceProcess;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Application.Register
@DeviceProcess.Register(OpenDevices.MOD_ID + ":test")
public class TestProcess implements DeviceProcess<Computer>, Application
{
    public static final int TEST_LAYOUT = 0;

    private Computer computer;
    private UUID processId;
    private WindowLayoutManager layoutManager;
    private WindowHandle window;
    private WindowHandle window2;

    public TestProcess(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
        this.layoutManager = new WindowLayoutManager(this.computer, this::synchronizeClients);
        this.window = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);
        this.window2 = new WindowHandle(this.computer.getWindowManager(), this.computer.getTaskBar(), this.processId);

        Layout testLayout = new Layout();
        testLayout.addComponent(new TextComponent(0, 0, Minecraft.DEFAULT_FONT_RENDERER_NAME, new StringTextComponent("dik"))); // TODO cannot access client classes from server! Add a layout provider or smth that allows the returning of created layouts for the client but not on the server.
        this.layoutManager.addLayout(TEST_LAYOUT, new Layout());
    }

    @Override
    public void init()
    {
        AppInfo info = this.getInfo();
        if (this.window.create())
        {
            this.window.center();
            this.window.setTitle(info.getName() + " v" + info.getVersion());
        }

        if (this.window2.create())
        {
            this.window2.center();
            this.window2.setTitle("Authors: " + Arrays.toString(info.getAuthors()));
        }

        this.synchronizeClients();
    }

    @Override
    public void update()
    {
        if (this.window2.exists() && this.window2.isCloseRequested())
        {
            this.window2.close();
        }
    }

    public WindowHandle getWindow()
    {
        return window;
    }

    public WindowHandle getWindow2()
    {
        return window2;
    }

    @Override
    public boolean isTerminated()
    {
        return this.window.isCloseRequested();
    }

    @Override
    public Computer getDevice()
    {
        return computer;
    }

    @Override
    public UUID getProcessId()
    {
        return processId;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layoutManager", this.layoutManager.serializeNBT());
        nbt.put("window", this.window.serializeNBT());
        nbt.put("window2", this.window2.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.layoutManager.deserializeNBT(nbt.getList("layoutManager", Constants.NBT.TAG_COMPOUND));
        this.window.deserializeNBT(nbt.getCompound("window"));
        this.window2.deserializeNBT(nbt.getCompound("window2"));
    }

    @Override
    public CompoundNBT writeSyncNBT()
    {
        return this.serializeNBT();
    }

    @Override
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    @Nullable
    @Override
    public Layout getLayout(UUID windowId)
    {
        return this.layoutManager.getCurrentLayout(windowId);
    }
}