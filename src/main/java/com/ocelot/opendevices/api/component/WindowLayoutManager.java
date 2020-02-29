package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.computer.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * <p>Manages the binding and unbinding of {@link Layout} for {@link Window}. Not required to be used but helps manage layouts.</p>
 *
 * @author Ocelot
 * @see Layout
 * @see Window
 */
public class WindowLayoutManager implements INBTSerializable<ListNBT>
{
    private Executor executor;
    private Runnable markDirty;
    private Map<Integer, Layout> layouts;
    private Map<UUID, Integer> currentLayouts;

    public WindowLayoutManager(Executor executor, Runnable markDirty)
    {
        this.executor = executor;
        this.markDirty = markDirty;
        this.layouts = new HashMap<>();
        this.currentLayouts = new HashMap<>();
    }

    @Nullable
    private Layout getLayout(int layoutId)
    {
        if (layoutId == -1)
            return null;
        return this.layouts.get(layoutId);
    }

    private void setLayout(UUID windowId, int layoutId)
    {
        int previousLayoutId = this.currentLayouts.getOrDefault(windowId, -1);
        if (previousLayoutId == layoutId)
            return;

        Layout previousLayout = this.getLayout(previousLayoutId);
        Layout layout = this.getLayout(layoutId);

        if (previousLayout != null)
            previousLayout.onLayoutUnload();
        if (layout != null)
        {
            this.currentLayouts.put(windowId, layoutId);
            layout.onLayoutLoad();
        }
        else
        {
            this.currentLayouts.remove(windowId);
        }
        this.markDirty.run();
    }

    /**
     * Binds the specified layout to the specified id. Used for {@link #setCurrentLayout(UUID, int)} and {@link #setCurrentLayout(UUID, int, boolean)}.
     *
     * @param id     The id to use when setting the layout
     * @param layout THe layout to bind to the id
     */
    public void addLayout(int id, Layout layout)
    {
        if (this.layouts.containsKey(id))
            throw new IllegalArgumentException("Layout with id '" + id + "' already exists!");
        this.layouts.put(id, layout);
    }

    /**
     * Sets the current layout for the specified window.
     *
     * @param windowId The window to set the layout for
     * @param layoutId The id of the layout
     */
    public void setCurrentLayout(UUID windowId, int layoutId)
    {
        this.setCurrentLayout(windowId, layoutId, false);
    }

    /**
     * Sets the current layout for the specified window.
     *
     * @param windowId   The window to set the layout for
     * @param layoutId   The id of the layout
     * @param executeNow Whether or not to execute this task now or the next tick
     */
    public void setCurrentLayout(UUID windowId, int layoutId, boolean executeNow)
    {
        if (executeNow)
        {
            this.setLayout(windowId, layoutId);
        }
        else
        {
            this.executor.execute(() -> this.setLayout(windowId, layoutId));
        }
    }

    /**
     * Checks the currently bound layouts for the layout for the specified window or null for no layout.
     *
     * @param windowId The id of the window to check
     * @return The layout found or null if the window has no bound layout
     */
    @Nullable
    public Layout getCurrentLayout(UUID windowId)
    {
        return this.getLayout(this.currentLayouts.get(windowId));
    }

    @Override
    public ListNBT serializeNBT()
    {
        ListNBT nbt = new ListNBT();
        this.currentLayouts.forEach((windowId, layoutId) ->
        {
            CompoundNBT layoutNbt = new CompoundNBT();
            layoutNbt.putUniqueId("windowId", windowId);
            layoutNbt.putInt("layoutId", layoutId);
            nbt.add(layoutNbt);
        });
        return nbt;
    }

    @Override
    public void deserializeNBT(ListNBT nbt)
    {
        for (int i = 0; i < nbt.size(); i++)
        {
            CompoundNBT layoutNbt = nbt.getCompound(i);
            this.currentLayouts.put(layoutNbt.getUniqueId("windowId"), layoutNbt.getInt("layoutId"));
        }
    }
}
