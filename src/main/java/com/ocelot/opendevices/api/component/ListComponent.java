package com.ocelot.opendevices.api.component;

import com.google.common.collect.ImmutableList;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.util.ScrollHandler;
import com.ocelot.opendevices.api.util.SyncHelper;
import io.github.ocelot.client.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

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
    private final int height;
    private final int visibleHeight;
    private final ScrollHandler scrollHandler;
    private boolean visible;

    private Renderer<E> renderer;
    private final List<E> items;
    private Function<E, CompoundNBT> serializer;
    private Function<CompoundNBT, E> deserializer;

    public ListComponent(float x, float y, int width, int height, int visibleHeight)
    {
        this.setValueSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = Math.min(visibleHeight, height);
        this.visibleHeight = visibleHeight;
        this.scrollHandler = new ScrollHandler(() -> this.getValueSerializer().markDirty("scroll"), height, visibleHeight);
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
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("scroll", nbt -> nbt.put("scroll", this.scrollHandler.serializeNBT()), nbt -> this.scrollHandler.deserializeNBT(nbt.getCompound("scroll")));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));

            syncHelper.addSerializer("items", nbt ->
            {
                if (this.serializer == null)
                {
                    OpenDevices.LOGGER.warn("No serializer was defined for component with " + this.items + ".");
                    return;
                }

                ListNBT itemsNbt = new ListNBT();
                this.items.forEach(item -> itemsNbt.add(this.serializer.apply(item)));
                nbt.put("items", itemsNbt);
            }, nbt ->
            {
                if (this.deserializer == null)
                {
                    OpenDevices.LOGGER.warn("No deserializer was defined for list component with " + this.items + ".");
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
        if (this.isHovered(mouseX, mouseY) && this.height > this.visibleHeight)
        {
            float delta = this.scrollHandler.getNextScroll() - this.scrollHandler.getScroll();
            float scrollAmount = (float) Math.min(Math.abs(amount), MAX_SCROLL) * this.scrollHandler.getScrollSpeed();
            float newScroll = Math.abs(delta) + scrollAmount;
            float finalScroll = (amount < 0 ? -1 : 1) * newScroll;
            float scroll = MathHelper.clamp(this.scrollHandler.getScroll() - finalScroll, 0, this.height - this.visibleHeight);
            if (this.scrollHandler.getScroll() != scroll)
            {
                this.scrollHandler.scroll(finalScroll);
                this.getValueSerializer().markDirty("scroll");
                return true;
            }
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
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean remove(Object o)
    {
        boolean value = this.items.remove(o);
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
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        boolean value = this.items.addAll(index, c);
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean value = this.items.removeAll(c);
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean value = this.items.retainAll(c);
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public void clear()
    {
        this.items.clear();
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
        this.getValueSerializer().markDirty("items");
        return value;
    }

    @Override
    public void add(int index, E element)
    {
        this.items.add(index, element);
        this.getValueSerializer().markDirty("items");
    }

    @Override
    public E remove(int index)
    {
        E value = this.items.remove(index);
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
        void render(ListComponent<T> list, T item, float posX, float posY, int width, int height, float partialTicks);

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
        void renderOverlay(TooltipRenderer renderer, ListComponent<T> list, T item, float posX, float posY, int width, int height, float partialTicks);
    }
}
