package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.concurrent.TimeUnit;

/**
 * <p>Allows the rendering of {@link ItemStack} in the frame.</p>
 *
 * @author Ocelot
 * @see ItemStack
 * @deprecated TODO Move to {@link ImageComponent} with FBO
 */
public class ItemStackComponent extends StandardComponent
{
    private float x;
    private float y;
    private int size;
    private ItemStack stack;

    private long tooltipDelay;
    private boolean visible;

    private ITextComponent stackText;
    private long lastTooltip;

    public ItemStackComponent(float x, float y, ItemStack stack)
    {
        this(x, y, 16, stack);
    }

    public ItemStackComponent(float x, float y, int size, ItemStack stack)
    {
        this.setClientSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.size = size;
        this.stack = stack;

        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.visible = true;

        this.stackText = this.stack.getTextComponent();
        this.lastTooltip = Long.MAX_VALUE;
    }

    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("size", nbt -> nbt.putInt("size", this.size), nbt -> this.size = nbt.getInt("size"));
            syncHelper.addSerializer("stack", nbt -> nbt.put("stack", this.stack.serializeNBT()), nbt ->
            {
                this.stack = ItemStack.read(nbt.getCompound("stack"));
                this.stackText = this.stack.getTextComponent();
            });

            syncHelper.addSerializer("tooltipDelay", nbt -> nbt.putLong("tooltipDelay", this.tooltipDelay), nbt -> this.tooltipDelay = nbt.getLong("tooltipDelay"));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));
        }
        return syncHelper;
    }

    @Override
    public void update()
    {
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        if (this.visible)
        {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            {
                GlStateManager.translatef(posX + this.x, posY + this.y, 100);

                GlStateManager.pushMatrix();
                {
                    GlStateManager.scalef(this.size / 16f, this.size / 16f, 1); // TODO render into a fbo and move to image component?
                    Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.stack, 0, 0);
                }
                GlStateManager.popMatrix();

                Minecraft.getInstance().getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, this.stack, 0, 0);
                GlStateManager.enableAlphaTest();
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible && this.tooltipDelay != Long.MAX_VALUE)
        {
            if (this.isHovered(mouseX - posX, mouseY - posY))
            {
                if (this.lastTooltip == Long.MAX_VALUE)
                    this.lastTooltip = System.currentTimeMillis();
                if (System.currentTimeMillis() - this.lastTooltip >= this.tooltipDelay)
                    renderer.renderComponentHoverEffect(this.stackText, mouseX, mouseY);
            }
            else
            {
                this.lastTooltip = Long.MAX_VALUE;
            }
        }
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
        return size;
    }

    @Override
    public int getHeight()
    {
        return size;
    }

    /**
     * @return The x and y size of the component
     */
    public int getSize()
    {
        return size;
    }

    /**
     * @return The stack rendering in this component. Should not be modified using this.
     */
    public ItemStack getStack()
    {
        return stack;
    }

    /**
     * @return The time it takes for tooltips to begin rendering in ms
     */
    public long getTooltipDelay()
    {
        return tooltipDelay;
    }

    /**
     * @return Whether or not the tooltip of this stack can be seen
     */
    public boolean isTooltipVisible()
    {
        return this.tooltipDelay != Long.MAX_VALUE;
    }

    /**
     * @return Whether or not this stack can be seen
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public ItemStackComponent setX(float x)
    {
        this.x = x;
        this.getClientSerializer().markDirty("x");
        return this;
    }

    /**
     * Sets the y position of this component to the specified value.
     *
     * @param y The new y position
     */
    public ItemStackComponent setY(float y)
    {
        this.y = y;
        this.getClientSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the position of this component to the specified values.
     *
     * @param x The new x position
     * @param y The new y position
     */
    public ItemStackComponent setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.getClientSerializer().markDirty("x");
        this.getClientSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the size of this component to the specified value.
     *
     * @param size The new width and height
     */
    public ItemStackComponent setSize(int size)
    {
        this.size = size;
        this.getClientSerializer().markDirty("size");
        return this;
    }

    /**
     * Sets the stack of this component to the specified value.
     *
     * @param stack The new item stack value
     */
    public ItemStackComponent setStack(ItemStack stack)
    {
        this.stack = stack.copy();
        this.stackText = this.stack.getTextComponent();
        this.getClientSerializer().markDirty("stack");
        return this;
    }

    /**
     * Sets the amount of time in the specified time unit it takes for a tooltip to begin rendering.
     *
     * @param unit         The time unit to use
     * @param tooltipDelay The time it takes for tooltips to begin rendering
     */
    public ItemStackComponent setTooltipDelay(TimeUnit unit, long tooltipDelay)
    {
        this.tooltipDelay = Math.max(0, unit.toMillis(tooltipDelay));
        this.getClientSerializer().markDirty("tooltipDelay");
        return this;
    }

    /**
     * Prevents the stack tooltip from rendering.
     */
    public ItemStackComponent setTooltipHidden()
    {
        this.setTooltipDelay(TimeUnit.MILLISECONDS, Long.MAX_VALUE);
        return this;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public ItemStackComponent setVisible(boolean visible)
    {
        this.visible = visible;
        this.getClientSerializer().markDirty("visible");
        return this;
    }
}
