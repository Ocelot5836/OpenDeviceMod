package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.computer.window.Window;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class WindowLayoutManager
{
    private Executor executor;
    private Map<UUID, Layout> layouts;

    public WindowLayoutManager(Executor executor)
    {
        this.executor = executor;
    }

    private void setLayout(UUID windowId, @Nullable Layout layout)
    {
        Layout previousLayout = this.layouts.get(windowId);
        if (previousLayout != null)
            previousLayout.onLayoutUnload();
        if (layout != null)
        {
            this.layouts.put(windowId, layout);
            layout.onLayoutLoad();
        }
        else
        {
            this.layouts.remove(windowId);
        }
    }

    public void setCurrentLayout(Window window, @Nullable Layout layout)
    {
        this.setCurrentLayout(window.getId(), layout, false);
    }

    public void setCurrentLayout(Window window, @Nullable Layout layout, boolean executeNow)
    {
        this.setCurrentLayout(window.getId(), layout, executeNow);
    }

    public void setCurrentLayout(UUID windowId, @Nullable Layout layout)
    {
        this.setCurrentLayout(windowId, layout, false);
    }

    public void setCurrentLayout(UUID windowId, @Nullable Layout layout, boolean executeNow)
    {
        if (executeNow)
        {
            this.setLayout(windowId, layout);
        }
        else
        {
            this.executor.execute(() -> this.setLayout(windowId, layout));
        }
    }

    public Layout getLayout(UUID windowId)
    {
        return layouts.get(windowId);
    }
}
