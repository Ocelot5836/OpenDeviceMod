package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

/**
 * <p>A {@link Layout} with a scroll bar as to allow for more components than can fit on screen.</p>
 *
 * @author Ocelot
 * @see Layout
 */
public class ScrollableLayout extends Layout
{
    public static final int DEFAULT_SCROLLBAR_COLOR = 0x5AFFFFFF;
    public static final float DEFAULT_SCROLL_SPEED = 5;
    public static final float TRANSITION_SPEED = 0.5f;
    public static final float MIN_SNAP = 0.1f;
    public static final float MAX_SCROLL = 2f;

    private int physicalHeight;
    private int scrollbarColor;
    private float scroll;
    private float scrollSpeed;

    private float lastScroll;
    private float nextScroll;
    private boolean selected;

    public ScrollableLayout(int height)
    {
        this(0, 0, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, height, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT);
    }

    public ScrollableLayout(int width, int height, int visibleHeight)
    {
        this(0, 0, width, height, visibleHeight);
    }

    public ScrollableLayout(float x, float y, int width, int height, int visibleHeight)
    {
        super(x, y, width, Math.min(visibleHeight, height));
        this.physicalHeight = height;
        this.scrollbarColor = DEFAULT_SCROLLBAR_COLOR;
        this.scroll = 0;
        this.scrollSpeed = DEFAULT_SCROLL_SPEED;

        this.selected = false;
    }

    @Override
    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = super.createSyncHelper();
        syncHelper.addSerializer("physicalHeight", nbt -> nbt.putInt("physicalHeight", this.physicalHeight), nbt -> this.physicalHeight = nbt.getInt("physicalHeight"));
        syncHelper.addSerializer("scrollbarColor", nbt -> nbt.putInt("scrollbarColor", this.scrollbarColor), nbt -> this.scrollbarColor = nbt.getInt("scrollbarColor"));
        syncHelper.addSerializer("scroll", nbt -> nbt.putFloat("scroll", this.scroll), nbt -> this.scroll = nbt.getFloat("scroll"));
        syncHelper.addSerializer("nextScroll", nbt -> nbt.putFloat("nextScroll", this.nextScroll), nbt -> this.nextScroll = nbt.getFloat("nextScroll"));
        return syncHelper;
    }

    @Override
    public void update()
    {
        super.update();
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
        if (this.scroll < 0 || this.scroll >= this.physicalHeight - this.getHeight())
        {
            this.setScroll(0);
        }
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        float interpolatedScroll = this.getInterpolatedScroll(partialTicks);

        RenderUtil.pushScissor(posX + this.getX(), posY + this.getY(), this.getWidth(), this.getHeight());
        this.components.forEach(component ->
        {
            if ((component.getX() + component.getWidth() >= this.getX() || component.getX() < this.getX() + this.getWidth()) && (component.getY() + component.getHeight() - interpolatedScroll >= this.getY() || component.getY() - interpolatedScroll < this.getY() + this.getHeight()))
            {
                component.render(posX + this.getX(), posY + this.getY() - interpolatedScroll, mouseX, mouseY, main && this.isHovered(mouseX - (int) posX, mouseY - (int) posY), partialTicks);
            }
        });
        RenderUtil.popScissor();

        if (this.scrollbarColor != 0 && this.physicalHeight > this.getHeight())
        {
            int scrollBarHeight = Math.max(20, (int) (this.getHeight() / (float) this.physicalHeight * (float) this.getHeight()));
            float scrollPercentage = MathHelper.clamp(interpolatedScroll / (float) (this.physicalHeight - this.getHeight()), 0.0F, 1.0F);
            float scrollBarY = (this.getHeight() - scrollBarHeight) * scrollPercentage;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(posX + this.getX() + this.getWidth() - 5, posY + this.getY() + scrollBarY, 0);
            Screen.fill(0, 0, 3, scrollBarHeight, this.scrollbarColor);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        float interpolatedScroll = this.getInterpolatedScroll(partialTicks);

        if (this.isHovered(mouseX - (int) posX, mouseY - (int) posY))
        {
            this.components.forEach(component ->
            {
                if ((component.getX() + component.getWidth() >= this.getX() || component.getX() < this.getX() + this.getWidth()) && (component.getY() + component.getHeight() - interpolatedScroll >= this.getY() || component.getY() - interpolatedScroll < this.getY() + this.getHeight()))
                {
                    component.renderOverlay(renderer, posX + this.getX(), posY + this.getY() - interpolatedScroll, mouseX, mouseY, partialTicks);
                }
            });
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        this.selected = this.isHovered(mouseX, mouseY);
        if (this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMousePressed(mouseX - this.getX(), mouseY - this.getY() + this.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        for (Component component : this.components)
        {
            if (component.onMouseReleased(mouseX - this.getX(), mouseY - this.getY() - this.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMouseScrolled(mouseX - this.getX(), mouseY - this.getY() - this.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), amount))
                {
                    return true;
                }
            }

            if (this.physicalHeight > this.getHeight())
            {
                float delta = this.nextScroll - this.scroll;
                float scrollAmount = (float) Math.min(Math.abs(amount), MAX_SCROLL) * this.scrollSpeed;
                float newScroll = Math.abs(delta) + scrollAmount;
                float finalScroll = (amount < 0 ? -1 : 1) * newScroll;
                float scroll = MathHelper.clamp(this.scroll - finalScroll, 0, this.physicalHeight - this.getHeight());
                if (this.scroll != scroll)
                {
                    this.nextScroll -= finalScroll;
                    this.getValueSerializer().markDirty("scroll");
                    this.getValueSerializer().markDirty("nextScroll");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            this.components.forEach(component -> component.onMouseMoved(mouseX - this.getX(), mouseY - this.getY() - this.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks())));
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMouseDragged(mouseX - this.getX(), mouseY - this.getY() - this.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton, deltaX, deltaY))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int mods)
    {
        if (this.selected)
        {
            for (Component component : this.components)
            {
                if (component.onKeyPressed(keyCode, scanCode, mods))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return The actual height of the layout
     */
    public int getPhysicalHeight()
    {
        return physicalHeight;
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
     * @return Sets the scrollbar to not render
     */
    public ScrollableLayout setScrollbarHidden()
    {
        this.setScrollbarColor(0);
        return this;
    }

    /**
     * Sets the color of the scroll bar to the provided color.
     *
     * @param scrollbarColor The new color of the scroll bar
     */
    public ScrollableLayout setScrollbarColor(int scrollbarColor)
    {
        this.scrollbarColor = scrollbarColor;
        this.getValueSerializer().markDirty("scrollbarColor");
        return this;
    }

    /**
     * Sets the position of the scroll bar.
     *
     * @param scroll The new scroll value
     */
    public ScrollableLayout setScroll(float scroll)
    {
        this.scroll = MathHelper.clamp(this.scroll, 0, this.physicalHeight - this.getHeight());
        this.nextScroll = this.scroll;
        this.getValueSerializer().markDirty("scroll");
        this.getValueSerializer().markDirty("nextScroll");
        return this;
    }

    /**
     * Sets the speed at which scrolling occurs.
     *
     * @param scrollSpeed The new scrolling speed
     */
    public ScrollableLayout setScrollSpeed(float scrollSpeed)
    {
        this.scrollSpeed = Math.max(scrollSpeed, 0);
        this.getValueSerializer().markDirty("scrollSpeed");
        return this;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public ScrollableLayout setVisible(boolean visible)
    {
        super.setVisible(visible);
        return this;
    }
}
