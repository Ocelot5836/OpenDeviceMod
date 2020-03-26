package com.ocelot.opendevices.api.component;

import com.google.common.collect.ImmutableList;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

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
    private float x;
    private float y;
    private int width;
    private int height;
    private int visibleHeight;
    private boolean visible;

    private Renderer<E> renderer;
    private List<E> items;
    private Function<E, CompoundNBT> serializer;
    private Function<CompoundNBT, E> deserializer;

    private float scroll;
    private float scrollSpeed;

    private float lastScroll;
    private float nextScroll;

    public ListComponent(float x, float y, int width, int height, int visibleHeight)
    {
        this.setClientSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = Math.min(visibleHeight, height);
        this.visibleHeight = visibleHeight;
        this.visible = true;

        this.renderer = null;
        this.items = new ArrayList<>();
        this.serializer = null;
        this.deserializer = null;
    }

    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));
        }
        return syncHelper;
    }

    @Override
    public void update()
    {
        if (this.renderer != null)
        {
            this.items.forEach(item -> this.renderer.update(this, item));
        }
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {

    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {

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

    public Renderer<E> getRenderer()
    {
        return renderer;
    }

    public Function<E, CompoundNBT> getSerializer()
    {
        return serializer;
    }

    public Function<CompoundNBT, E> getDeserializer()
    {
        return deserializer;
    }

    /**
     * @return The position of the scroll bar
     */
    public float getScroll()
    {
        return scroll;
    }

    /**
     * Calculates the position of the scroll bar based on where is was last tick and now.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The position of the scroll bar interpolated over the specified value
     */
    public float getInterpolatedScroll(float partialTicks)
    {
        return this.lastScroll + (this.scroll - this.lastScroll) * partialTicks;
    }

    /**
     * @return The speed at which scrolling takes place
     */
    public float getScrollSpeed()
    {
        return scrollSpeed;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public ListComponent<E> setVisible(boolean visible)
    {
        this.visible = visible;
        this.getClientSerializer().markDirty("visible");
        return this;
    }

    /**
     * Sets the serializer and deserializer for the items. Must be set in order to save and load items to the client.
     *
     * @param serializer   The function used to turn an item into NBT
     * @param deserializer The function used to turn NBT into an item
     */
    public void setItemSerializer(Function<E, CompoundNBT> serializer, Function<CompoundNBT, E> deserializer)
    {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Sets the position of the scroll bar.
     *
     * @param scroll The new scroll value
     */
    public ListComponent<E> setScroll(float scroll)
    {
        this.scroll = MathHelper.clamp(this.scroll, 0, this.height - this.visibleHeight);
        this.nextScroll = this.scroll;
        this.getClientSerializer().markDirty("scroll");
        this.getClientSerializer().markDirty("nextScroll");
        return this;
    }

    /**
     * Sets the speed at which scrolling occurs.
     *
     * @param scrollSpeed The new scrolling speed
     */
    public ListComponent<E> setScrollSpeed(float scrollSpeed)
    {
        this.scrollSpeed = Math.max(scrollSpeed, 0);
        this.getClientSerializer().markDirty("scrollSpeed");
        return this;
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
        boolean value = this.items.add(e);
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean remove(Object o)
    {
        boolean value = this.items.remove(o);
        this.getClientSerializer().markDirty("items");
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
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        boolean value = this.items.addAll(index, c);
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean value = this.items.removeAll(c);
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean value = this.items.retainAll(c);
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public void clear()
    {
        this.items.clear();
        this.getClientSerializer().markDirty("items");
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
        this.getClientSerializer().markDirty("items");
        return value;
    }

    @Override
    public void add(int index, E element)
    {
        this.items.add(index, element);
        this.getClientSerializer().markDirty("items");
    }

    @Override
    public E remove(int index)
    {
        E value = this.items.remove(index);
        this.getClientSerializer().markDirty("items");
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
        void update(ListComponent<T> component, T item);

        void render(ListComponent<T> component, T item, float posX, float posY, int width, int height, float partialTicks);

        void renderOverlay(TooltipRenderer renderer, ListComponent<T> list, T item, float posX, float posY, int width, int height, float partialTicks);
    }
}
