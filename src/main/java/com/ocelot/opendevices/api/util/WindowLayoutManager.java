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
    private Map<UUID, CompoundNBT> currentLayoutsData;
    @OnlyIn(Dist.CLIENT)
    private LayoutProvider layoutProvider;
    @OnlyIn(Dist.CLIENT)
    private Map<Integer, Layout> cachedLayouts;

    public WindowLayoutManager(Executor executor, @Nullable Runnable markDirty, Supplier<LayoutProvider> layoutProvider)
    {
        this.executor = executor;
        this.markDirty = markDirty;
        this.currentLayouts = new HashMap<>();
        this.currentLayoutsData = new HashMap<>();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            this.layoutProvider = layoutProvider.get();
            this.cachedLayouts = new HashMap<>();
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private Layout getLayout(int layoutId)
    {
        if (layoutId == -1)
            return null;
        return this.cachedLayouts.get(layoutId);
    }

    private void setLayout(UUID windowId, int layoutId, @Nullable CompoundNBT data, boolean markDirty)
    {
        int previousLayoutId = this.currentLayouts.getOrDefault(windowId, -1);
        if (previousLayoutId == layoutId)
        {
            if (data != null)
                this.currentLayoutsData.put(windowId, data);
            return;
        }

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            Layout previousLayout = this.getLayout(previousLayoutId);
            if (previousLayout != null)
                previousLayout.onLayoutUnload();
        });

        if (layoutId == -1)
        {
            this.currentLayouts.remove(windowId);
            this.currentLayoutsData.remove(windowId);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> this.cachedLayouts.remove(layoutId));
        }
        else
        {
            this.currentLayouts.put(windowId, layoutId);
            if (data != null)
                this.currentLayoutsData.put(windowId, data);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
            {
                Layout layout = this.getLayout(layoutId);
                if (layout != null)
                    layout.onLayoutLoad();
                Layout cachedLayout = this.layoutProvider.create(layoutId);
                cachedLayout.setDirty(false);
                if (cachedLayout.getValueSerializer() != null)
                {
                    ValueSerializer valueSerializer = cachedLayout.getValueSerializer();
                    valueSerializer.discardChanges();
                    valueSerializer.read(data);
                }
                this.cachedLayouts.put(layoutId, cachedLayout);
            });
        }
        if (markDirty && this.markDirty != null)
            this.markDirty.run();
    }

    /**
     * Updates the containing layout info for the client.
     */
    @OnlyIn(Dist.CLIENT)
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
            this.setLayout(windowId, layoutId, new CompoundNBT(), true);
        }
        else
        {
            this.executor.execute(() -> this.setLayout(windowId, layoutId, new CompoundNBT(), true));
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
        ListNBT currentLayoutsNbt = new ListNBT();
        this.currentLayouts.forEach((windowId, layoutId) ->
        {
            CompoundNBT layoutNbt = new CompoundNBT();
            layoutNbt.putUniqueId("windowId", windowId);
            layoutNbt.putInt("layoutId", layoutId);
            layoutNbt.put("data", this.currentLayoutsData.getOrDefault(windowId, new CompoundNBT()));
            currentLayoutsNbt.add(layoutNbt);
        });
        nbt.put("layouts", currentLayoutsNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.currentLayouts.clear();
        this.currentLayoutsData.clear();

        ListNBT currentLayoutsNbt = nbt.getList("layouts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < currentLayoutsNbt.size(); i++)
        {
            CompoundNBT layoutNbt = currentLayoutsNbt.getCompound(i);
            this.setLayout(layoutNbt.getUniqueId("windowId"), layoutNbt.getInt("layoutId"), layoutNbt.getCompound("data"), false);
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

        ListNBT currentLayoutsNbt = new ListNBT();
        this.currentLayouts.forEach((windowId, layoutId) ->
        {
            CompoundNBT layoutNbt = new CompoundNBT();
            layoutNbt.putUniqueId("windowId", windowId);
            layoutNbt.putInt("layoutId", layoutId);
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
            {
                Layout currentLayout = this.getCurrentLayout(windowId);
                if (currentLayout != null && currentLayout.getValueSerializer() != null)
                {
                    layoutNbt.put("data", currentLayout.getValueSerializer().write());
                }
            });
            currentLayoutsNbt.add(layoutNbt);
        });
        nbt.put("layouts", currentLayoutsNbt);

        ListNBT layoutsNbt = new ListNBT();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
                this.currentLayouts.forEach((windowId, layoutId) ->
                {
                    CompoundNBT layoutNbt = new CompoundNBT();
                    Layout currentLayout = this.getCurrentLayout(windowId);
                    if (currentLayout != null && currentLayout.getValueSerializer() != null)
                    {
                        layoutNbt.putUniqueId("windowId", windowId);
                        layoutNbt.put("data", currentLayout.getValueSerializer().writeClient());
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
        ListNBT currentLayoutsNbt = nbt.getList("layouts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < currentLayoutsNbt.size(); i++)
        {
            CompoundNBT layoutNbt = currentLayoutsNbt.getCompound(i);
            this.setLayout(layoutNbt.getUniqueId("windowId"), layoutNbt.getInt("layoutId"), layoutNbt.contains("data", Constants.NBT.TAG_COMPOUND) ? layoutNbt.getCompound("data") : null, false);
        }

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
                        currentLayout.getValueSerializer().readClient(data);
                    }
                }
            }
        });
    }

    /**
     * <p>Creates a new layout for the id provided.</p>
     *
     * @author Ocelot
     */
    @OnlyIn(Dist.CLIENT)
    public interface LayoutProvider
    {
        /**
         * Generates a new layout for the client using the specified id.
         *
         * @param id The id of the layout
         * @return The new layout instance
         */
        Layout create(int id);
    }
}
