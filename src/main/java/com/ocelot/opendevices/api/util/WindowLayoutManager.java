package com.ocelot.opendevices.api.util;

import com.ocelot.opendevices.api.component.Layout;
import com.ocelot.opendevices.api.computer.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Manages the binding and unbinding of {@link Layout} for {@link Window}. Not required to be used but helps manage layouts.</p>
 *
 * @author Ocelot
 * @see Layout
 * @see Window
 */
public class WindowLayoutManager implements INBTSerializable<CompoundNBT>
{
    private Executor executor;
    private Runnable markDirty;
    private Map<UUID, Integer> currentLayouts;
    @OnlyIn(Dist.CLIENT)
    private Supplier<Function<Integer, Layout>> layoutProvider;
    @OnlyIn(Dist.CLIENT)
    private Map<Integer, Layout> cachedLayouts;

    public WindowLayoutManager(Executor executor, @Nullable Runnable markDirty, Supplier<Function<Integer, Layout>> layoutProvider)
    {
        this.executor = executor;
        this.markDirty = markDirty;
        this.currentLayouts = new HashMap<>();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            this.layoutProvider = layoutProvider;
            this.cachedLayouts = new HashMap<>();
        });
    }

    private ListNBT writeCurrentLayouts()
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

    private void readCurrentLayouts(ListNBT nbt)
    {
        for (int i = 0; i < nbt.size(); i++)
        {
            CompoundNBT layoutNbt = nbt.getCompound(i);
            this.setLayout(layoutNbt.getUniqueId("windowId"), layoutNbt.getInt("layoutId"), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private Layout getLayout(int layoutId)
    {
        if (layoutId == -1)
            return null;
        return this.cachedLayouts.get(layoutId);
    }

    private void setLayout(UUID windowId, int layoutId, boolean markDirty)
    {
        int previousLayoutId = this.currentLayouts.getOrDefault(windowId, -1);
        if (previousLayoutId == layoutId)
            return;

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            Layout previousLayout = this.getLayout(previousLayoutId);
            if (previousLayout != null)
                previousLayout.onLayoutUnload();
        });

        if (layoutId == -1)
        {
            this.currentLayouts.remove(windowId);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> this.cachedLayouts.remove(layoutId));
        }
        else
        {
            this.currentLayouts.put(windowId, layoutId);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
            {
                Layout layout = this.getLayout(layoutId);
                if (layout != null)
                    layout.onLayoutLoad();
                this.cachedLayouts.put(layoutId, this.layoutProvider.get().apply(layoutId));
            });
        }
        if (markDirty && this.markDirty != null)
            this.markDirty.run();
    }

    /**
     * Updates the containing layout info for the client.
     */
    public void update()
    {
        if (this.markDirty != null)
        {
            boolean dirty = false;
            for (UUID windowId : this.currentLayouts.keySet())
            {
                Layout layout = this.getCurrentLayout(windowId);
                if (layout != null && layout.isDirty())
                {
                    layout.setDirty(false);
                    dirty = true;
                }
            }
            if (dirty)
            {
                this.markDirty.run();
            }
        }
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
            this.setLayout(windowId, layoutId, true);
        }
        else
        {
            this.executor.execute(() -> this.setLayout(windowId, layoutId, true));
        }
    }

    /**
     * Checks the currently bound layouts for the layout for the specified window or null for no layout.
     *
     * @param windowId The id of the window to check
     * @return The layout found or null if the window has no bound layout
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    public Layout getCurrentLayout(UUID windowId)
    {
        return this.getLayout(this.currentLayouts.getOrDefault(windowId, -1));
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layouts", this.writeCurrentLayouts());
        ListNBT layoutsNbt = new ListNBT();
        this.currentLayouts.forEach((windowId, layoutId) ->
        {
            CompoundNBT layoutNbt = new CompoundNBT();
            Layout currentLayout = this.getCurrentLayout(windowId);
            if (currentLayout != null && currentLayout.getValueSerializer() != null)
            {
                layoutNbt.putUniqueId("windowId", windowId);
                layoutNbt.put("data", currentLayout.getValueSerializer().save());
                layoutsNbt.add(layoutNbt);
            }
        });
        nbt.put("layoutsData", layoutsNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.readCurrentLayouts(nbt.getList("layouts", Constants.NBT.TAG_COMPOUND));
        ListNBT layoutsNbt = nbt.getList("layoutsData", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < layoutsNbt.size(); i++)
        {
            CompoundNBT layoutNbt = layoutsNbt.getCompound(i);
            UUID windowId = layoutNbt.getUniqueId("windowId");
            if (layoutNbt.contains("data", Constants.NBT.TAG_COMPOUND))
            {
                CompoundNBT data = layoutNbt.getCompound("data");
                Layout currentLayout = this.getCurrentLayout(windowId);
                if (currentLayout != null && currentLayout.getValueSerializer() != null)
                {
                    currentLayout.getValueSerializer().load(data);
                }
            }
        }
    }

    /**
     * Writes any data that will be needed.
     *
     * @return The tag full of data
     */
    public CompoundNBT writeSyncNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("layouts", this.writeCurrentLayouts());
        ListNBT layoutsNbt = new ListNBT();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
                this.currentLayouts.forEach((windowId, layoutId) ->
                {
                    CompoundNBT layoutNbt = new CompoundNBT();
                    Layout currentLayout = this.getCurrentLayout(windowId);
                    if (currentLayout != null && currentLayout.getValueSerializer() != null)
                    {
                        layoutNbt.putUniqueId("windowId", windowId);
                        layoutNbt.put("data", currentLayout.getValueSerializer().serializeNBT());
                        currentLayout.getValueSerializer().discardChanges();
                        layoutsNbt.add(layoutNbt);
                    }
                }));
        nbt.put("layoutsData", layoutsNbt);
        return nbt;
    }

    /**
     * Reads any data from the specified tag on the other logical side.
     *
     * @param nbt The tag to read from
     */
    public void readSyncNBT(CompoundNBT nbt)
    {
        this.readCurrentLayouts(nbt.getList("layouts", Constants.NBT.TAG_COMPOUND));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            ListNBT layoutsNbt = nbt.getList("layoutsData", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < layoutsNbt.size(); i++)
            {
                CompoundNBT layoutNbt = layoutsNbt.getCompound(i);
                UUID windowId = layoutNbt.getUniqueId("windowId");
                if (layoutNbt.contains("data", Constants.NBT.TAG_COMPOUND))
                {
                    CompoundNBT data = layoutNbt.getCompound("data");
                    Layout currentLayout = this.getCurrentLayout(windowId);
                    if (currentLayout != null && currentLayout.getValueSerializer() != null)
                    {
                        currentLayout.getValueSerializer().deserializeNBT(data);
                    }
                }
            }
        });
    }
}
