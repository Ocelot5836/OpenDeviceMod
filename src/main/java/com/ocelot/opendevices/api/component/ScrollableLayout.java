package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.SyncHelper;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.TooltipRenderer;
import io.github.ocelot.common.ScrollHandler;
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
    public static final double MAX_SCROLL = 2f;

    private final int physicalHeight;
    private final ScrollHandler scrollHandler;
    private int scrollbarColor;

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
        this.scrollHandler = new ScrollHandler(() -> this.getValueSerializer().markDirty("scroll"), height, visibleHeight);
        this.scrollbarColor = DEFAULT_SCROLLBAR_COLOR;

        this.selected = false;
    }

    @Override
    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = super.createSyncHelper();
        {
            syncHelper.addSerializer("scroll", nbt -> nbt.put("scroll", this.scrollHandler.serializeNBT()), nbt -> this.scrollHandler.deserializeNBT(nbt.getCompound("scroll")));
        }
        return syncHelper;
    }

    @Override
    public void update()
    {
        super.update();
        this.scrollHandler.update();
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        float interpolatedScroll = this.scrollHandler.getInterpolatedScroll(partialTicks);

        ScissorHelper.push(posX + this.getX(), posY + this.getY(), this.getWidth(), this.getHeight());
        this.components.forEach(component ->
        {
            if ((component.getX() + component.getWidth() >= this.getX() || component.getX() < this.getX() + this.getWidth()) && (component.getY() + component.getHeight() - interpolatedScroll >= this.getY() || component.getY() - interpolatedScroll < this.getY() + this.getHeight()))
            {
                component.render(posX + this.getX(), posY + this.getY() - interpolatedScroll, mouseX, mouseY, main && this.isHovered(mouseX - (int) posX, mouseY - (int) posY), partialTicks);
            }
        });
        ScissorHelper.pop();
        if(!ScissorHelper.isEmpty())
        {
            OpenDevices.LOGGER.error("A component did not pop it's scissor!");
            ScissorHelper.clear();
        }

        if (this.scrollbarColor != 0 && this.scrollHandler.getMaxScroll() > 0)
        {
            int scrollBarHeight = Math.max(20, (int) (this.getHeight() / (float) this.physicalHeight * (float) this.getHeight()));
            float scrollPercentage = MathHelper.clamp(interpolatedScroll / (float) (this.physicalHeight - this.getHeight()), 0.0F, 1.0F);
            float scrollBarY = (this.getHeight() - scrollBarHeight) * scrollPercentage;
            RenderSystem.pushMatrix();
            RenderSystem.translatef(posX + this.getX() + this.getWidth() - 5, posY + this.getY() + scrollBarY, 0);
            Screen.fill(0, 0, 3, scrollBarHeight, this.scrollbarColor);
            RenderSystem.popMatrix();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        float interpolatedScroll = this.scrollHandler.getInterpolatedScroll(partialTicks);

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
                if (component.onMousePressed(mouseX - this.getX(), mouseY - this.getY() + this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton))
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
            if (component.onMouseReleased(mouseX - this.getX(), mouseY - this.getY() - this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton))
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
                if (component.onMouseScrolled(mouseX - this.getX(), mouseY - this.getY() - this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), amount))
                {
                    return true;
                }
            }

            if (this.scrollHandler.getMaxScroll() > 0 && this.scrollHandler.mouseScrolled(MAX_SCROLL, amount))
            {
                this.getValueSerializer().markDirty("scroll");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            this.components.forEach(component -> component.onMouseMoved(mouseX - this.getX(), mouseY - this.getY() - this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks())));
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMouseDragged(mouseX - this.getX(), mouseY - this.getY() - this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton, deltaX, deltaY))
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
    public ScrollableLayout setVisible(boolean visible)
    {
        super.setVisible(visible);
        return this;
    }

    /**
     * Sets the color of the scroll bar if there is enough scroll
     *
     * @param scrollbarColor The new color of the scroll bar
     */
    public ScrollableLayout setScrollbarColor(int scrollbarColor)
    {
        this.scrollbarColor = scrollbarColor;
        return this;
    }
}
