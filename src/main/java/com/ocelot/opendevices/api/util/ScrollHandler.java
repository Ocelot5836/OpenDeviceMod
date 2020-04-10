package com.ocelot.opendevices.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * <p>Handles smooth scrolling automatically.</p>
 *
 * @author Ocelot
 */
public class ScrollHandler implements INBTSerializable<CompoundNBT>
{
    public static final int DEFAULT_SCROLLBAR_COLOR = 0x5AFFFFFF;
    public static final float DEFAULT_SCROLL_SPEED = 5;
    public static final float TRANSITION_SPEED = 0.5f;
    public static final float MIN_SNAP = 0.1f;

    private Runnable markDirty;
    private int height;
    private int visibleHeight;

    private int scrollbarColor;
    private float scroll;
    private float scrollSpeed;
    private float lastScroll;
    private float nextScroll;

    public ScrollHandler(@Nullable Runnable markDirty, int height, int visibleHeight)
    {
        this.markDirty = markDirty;
        this.height = height;
        this.visibleHeight = visibleHeight;

        this.scrollbarColor = DEFAULT_SCROLLBAR_COLOR;
        this.scroll = 0;
        this.scrollSpeed = DEFAULT_SCROLL_SPEED;
    }

    /**
     * Updates the smooth transition of scrolling.
     */
    public void update()
    {
        this.lastScroll = this.scroll;
        float delta = this.nextScroll - this.scroll;
        if (Math.abs(delta) < MIN_SNAP)
        {
            this.scroll = this.nextScroll;
        }
        else
        {
            this.scroll += delta * TRANSITION_SPEED;
        }
        if (this.scroll < 0 || this.scroll >= this.height - this.visibleHeight)
        {
            this.setScroll(0);
        }
    }

    /**
     * Scrolls the specified amount over time.
     *
     * @param scrollAmount The amount to scroll
     */
    public ScrollHandler scroll(float scrollAmount)
    {
        this.nextScroll -= scrollAmount;
        return this;
    }

    /**
     * @return The color of the scroll bar
     */
    public int getScrollbarColor()
    {
        return scrollbarColor;
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
     * @return The scrolling value last tick
     */
    public float getLastScroll()
    {
        return lastScroll;
    }

    /**
     * @return The scroll value being animated to
     */
    public float getNextScroll()
    {
        return nextScroll;
    }

    /**
     * @return Sets the scrollbar to not render
     */
    public ScrollHandler setScrollbarHidden()
    {
        this.setScrollbarColor(0);
        return this;
    }

    /**
     * Sets the color of the scroll bar to the provided color.
     *
     * @param scrollbarColor The new color of the scroll bar
     */
    public ScrollHandler setScrollbarColor(int scrollbarColor)
    {
        this.scrollbarColor = scrollbarColor;
        if (this.markDirty != null)
            this.markDirty.run();
        return this;
    }

    /**
     * Sets the position of the scroll bar.
     *
     * @param scroll The new scroll value
     */
    public ScrollHandler setScroll(float scroll)
    {
        this.scroll = MathHelper.clamp(scroll, 0, this.height - this.visibleHeight);
        this.nextScroll = this.scroll;
        if (this.markDirty != null)
            this.markDirty.run();
        return this;
    }

    /**
     * Sets the speed at which scrolling occurs.
     *
     * @param scrollSpeed The new scrolling speed
     */
    public ScrollHandler setScrollSpeed(float scrollSpeed)
    {
        this.scrollSpeed = Math.max(scrollSpeed, 0);
        if (this.markDirty != null)
            this.markDirty.run();
        return this;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("scrollbarColor", this.scrollbarColor);
        nbt.putFloat("scroll", this.scroll);
        nbt.putFloat("scrollSpeed", this.scrollSpeed);
        nbt.putFloat("nextScroll", this.nextScroll);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.scrollbarColor = nbt.getInt("scrollbarColor");
        this.scroll = nbt.getFloat("scroll");
        this.scrollSpeed = nbt.getFloat("scrollSpeed");
        this.nextScroll = nbt.getFloat("nextScroll");
    }
}
