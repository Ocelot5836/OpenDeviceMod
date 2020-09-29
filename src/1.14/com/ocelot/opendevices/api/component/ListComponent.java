package com.ocelot.opendevices.api.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.util.SyncHelper;
import io.github.ocelot.client.FontHelper;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.TooltipRenderer;
import io.github.ocelot.common.ScrollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * <p>A component that allows for the rendering of items in a list.</p>
 * <p>A custom renderer can be specified using {@link Renderer}.</p>
 *
 * @param <E> The type of items stored in this list
 * @author Ocelot
 */
public class ListComponent<E> extends StandardComponent implements List<E>
{
    public static final float MAX_SCROLL = 2f;

    private float x;
    private float y;
    private final int width;
    private int height;
    private final int visibleHeight;
    private ScrollHandler scrollHandler;
    private boolean visible;

    private Renderer<E> renderer;
    private int itemHeight;
    private final List<E> items;
    private Function<E, CompoundNBT> serializer;
    private Function<CompoundNBT, E> deserializer;

    public ListComponent(float x, float y, int width, int visibleHeight)
    {
        this.setValueSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 0;
        this.visibleHeight = visibleHeight;
        this.scrollHandler = new ScrollHandler(() -> this.getValueSerializer().markDirty("scroll"), this.height, visibleHeight);
        this.visible = true;

        this.renderer = new DefaultRenderer<>();
        this.itemHeight = 16;
        this.items = new ArrayList<>();
        this.serializer = null;
        this.deserializer = null;
    }

    private void updateHeight()
    {
        this.height = Math.max(0, this.items.size() * (this.itemHeight + 1) - 1);
        float scroll = this.scrollHandler.getScroll();
        float scrollSpeed = this.scrollHandler.getScrollSpeed();
        this.scrollHandler = new ScrollHandler(() -> this.getValueSerializer().markDirty("scroll"), this.height, this.visibleHeight);
        this.scrollHandler.setScroll(scroll);
        this.scrollHandler.setScrollSpeed(scrollSpeed);
        this.getValueSerializer().markDirty("height");
    }

    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("height", nbt -> nbt.putInt("height", this.height), nbt -> this.height = nbt.getInt("height"));
            syncHelper.addSerializer("scroll", nbt -> nbt.put("scroll", this.scrollHandler.serializeNBT()), nbt -> this.scrollHandler.deserializeNBT(nbt.getCompound("scroll")));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));

            syncHelper.addSerializer("itemHeight", nbt -> nbt.putInt("itemHeight", this.itemHeight), nbt -> this.itemHeight = nbt.getInt("scroll"));
            syncHelper.addSerializer("items", nbt ->
            {
                if (this.serializer == null)
                {
                    if (this.deserializer != null)
                        OpenDevices.LOGGER.warn("No serializer was defined for component with " + this.items + " but a deserializer was set.");
                    return;
                }

                ListNBT itemsNbt = new ListNBT();
                this.items.forEach(item -> itemsNbt.add(this.serializer.apply(item)));
                nbt.put("items", itemsNbt);
            }, nbt ->
            {
                if (this.deserializer == null)
                {
                    if (this.serializer != null)
                        OpenDevices.LOGGER.warn("No deserializer was defined for list component with " + this.items + " but a serializer was set.");
                    return;
                }

                this.items.clear();

                ListNBT itemsNbt = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < itemsNbt.size(); i++)
                {
                    this.items.add(this.deserializer.apply(itemsNbt.getCompound(i)));
                }
            });
        }
        return syncHelper;
    }

    @Override
    public void update()
    {
        this.scrollHandler.update();
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        if (this.visible)
        {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(posX + this.x, posY + this.y, 0);
            fill(0, 0, this.width, this.height, 0xFFFF00FF);
            RenderSystem.popMatrix();

            float interpolatedScroll = this.scrollHandler.getInterpolatedScroll(partialTicks);

            ScissorHelper.push(posX + this.getX(), posY + this.getY(), this.getWidth(), this.getHeight());
            this.items.forEach(item -> this.renderer.render(this, item, posX + this.getX(), posY + this.getY() - interpolatedScroll, this.width - 2, this.itemHeight, mouseX, mouseY, main, partialTicks));
            ScissorHelper.pop();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {

        }
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.isHovered(mouseX, mouseY) && this.scrollHandler.getMaxScroll() > 0 && this.scrollHandler.mouseScrolled(MAX_SCROLL, amount))
        {
            this.getValueSerializer().markDirty("scroll");
            return true;
        }
        return false;
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return visibleHeight;
    }

    /**
     * @return The actual height of the list
     */
    public int getPhysicalHeight()
    {
        return height;
    }

    /**
     * @return Whether or not this component can be seen and interacted with
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * @return The current item renderer
     */
    public Renderer<E> getRenderer()
    {
        return renderer;
    }

    /**
     * @return The item serializer
     */
    @Nullable
    public Function<E, CompoundNBT> getSerializer()
    {
        return serializer;
    }

    /**
     * @return The item deserializer
     */
    @Nullable
    public Function<CompoundNBT, E> getDeserializer()
    {
        return deserializer;
    }

    /**
     * @return The manager for scrolling
     */
    public ScrollHandler getScrollHandler()
    {
        return scrollHandler;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public ListComponent<E> setVisible(boolean visible)
    {
        this.visible = visible;
        this.getValueSerializer().markDirty("visible");
        return this;
    }

    /**
     * Sets the renderer for the items.
     *
     * @param renderer   The new item renderer
     * @param itemHeight The new height of each item in the list
     */
    public ListComponent<E> setRenderer(@Nullable Renderer<E> renderer, int itemHeight)
    {
        this.renderer = renderer;
        this.itemHeight = itemHeight;
        this.updateHeight();
        this.getValueSerializer().markDirty("itemHeight");
        return this;
    }

    /**
     * Sets the serializer and deserializer for the items. Must be set in order to save and load items to the client.
     *
     * @param serializer   The function used to turn an item into NBT
     * @param deserializer The function used to turn NBT into an item
     */
    public void setItemSerializer(@Nullable Function<E, CompoundNBT> serializer, @Nullable Function<CompoundNBT, E> deserializer)
    {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public int size()
    {
        return this.items.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.items.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return this.items.contains(o);
    }

    @Override
    public Iterator<E> iterator()
    {
        return this.items.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return this.items.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.items.toArray(a);
    }

    @Override
    public boolean add(E e)
    {
        this.items.add(e);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        boolean value = this.items.remove(o);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return this.items.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        boolean value = this.items.addAll(c);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        boolean value = this.items.addAll(index, c);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean value = this.items.removeAll(c);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean value = this.items.retainAll(c);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public void clear()
    {
        this.items.clear();
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
    }

    @Override
    public E get(int index)
    {
        return this.items.get(index);
    }

    @Override
    public E set(int index, E element)
    {
        E value = this.items.set(index, element);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public void add(int index, E element)
    {
        this.items.add(index, element);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
    }

    @Override
    public E remove(int index)
    {
        E value = this.items.remove(index);
        this.updateHeight();
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public int indexOf(Object o)
    {
        return this.items.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return this.items.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator()
    {
        return this.items.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index)
    {
        return this.items.listIterator(index);
    }

    @Override
    public ImmutableList<E> subList(int fromIndex, int toIndex)
    {
        return ImmutableList.copyOf(this.items.subList(fromIndex, toIndex));
    }

    /**
     * <p>A configurable function that allows the custom rendering of items inside of a {@link ListComponent}.</p>
     *
     * @param <T> The type of items being rendered
     * @author Ocelot
     * @see ListComponent
     */
    public interface Renderer<T>
    {
        /**
         * Updates the specified item within the list.
         *
         * @param list The component holding the item
         * @param item The item to be updated
         */
        void update(ListComponent<T> list, T item);

        /**
         * Renders the specified item within the list.
         *
         * @param list         The component holding the item
         * @param item         The item to render
         * @param posX         The x position of the box
         * @param posY         The y position of the box
         * @param width        The width of the box
         * @param height       The height of the box
         * @param partialTicks The percentage from last update and this update
         */
        void render(ListComponent<T> list, T item, float posX, float posY, int width, int height, int mouseX, int mouseY, boolean main, float partialTicks);

        /**
         * Renders the tooltip of the specified item within the list.
         *
         * @param renderer     The renderer used to draw tooltips
         * @param list         The component holding the item
         * @param item         The item to render
         * @param posX         The x position of the box
         * @param posY         The y position of the box
         * @param width        The width of the box
         * @param height       The height of the box
         * @param partialTicks The percentage from last update and this update
         */
        void renderOverlay(TooltipRenderer renderer, ListComponent<T> list, T item, float posX, float posY, int width, int height, int mouseX, int mouseY, float partialTicks);
    }

    private static class DefaultRenderer<E> implements Renderer<E>
    {
        @Override
        public void update(ListComponent<E> list, E item)
        {
        }

        @Override
        public void render(ListComponent<E> list, E item, float posX, float posY, int width, int height, int mouseX, int mouseY, boolean main, float partialTicks)
        {
            FontHelper.drawStringClipped(Minecraft.getInstance().fontRenderer, String.valueOf(item), posX, posY, width, 0xffff00ff, false);
        }

        @Override
        public void renderOverlay(TooltipRenderer renderer, ListComponent<E> list, E item, float posX, float posY, int width, int height, int mouseX, int mouseY, float partialTicks)
        {
        }
    }
}
