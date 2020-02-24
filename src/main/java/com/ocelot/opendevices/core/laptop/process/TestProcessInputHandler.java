package com.ocelot.opendevices.core.laptop.process;

import com.ocelot.opendevices.api.device.ProcessInputHandler;
import com.ocelot.opendevices.api.laptop.Computer;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class TestProcessInputHandler implements ProcessInputHandler<Computer, TestProcess>
{
    @Override
    public boolean onKeyPressed(TestProcess process, @Nullable UUID windowId, int keyCode, int scanCode, int mods)
    {
        return true;
    }

    @Override
    public boolean onKeyReleased(TestProcess process, @Nullable UUID windowId, int keyCode, int scanCode, int mods)
    {
        return true;
    }

    @Override
    public boolean onMouseDragged(TestProcess process, @Nullable UUID windowId, double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return true;
    }

    @Override
    public boolean onMousePressed(TestProcess process, @Nullable UUID windowId, double mouseX, double mouseY, int mouseButton)
    {
        return true;
    }

    @Override
    public boolean onMouseReleased(TestProcess process, @Nullable UUID windowId, double mouseX, double mouseY, int mouseButton)
    {
        return true;
    }

    @Override
    public boolean onMouseScrolled(TestProcess process, @Nullable UUID windowId, double mouseX, double mouseY, double amount)
    {
        return true;
    }

    @Override
    public void onMouseMoved(TestProcess process, @Nullable UUID windowId, double mouseX, double mouseY)
    {
    }
}
