package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;

/**
 * <p>A spinner that can be used in place of loading components.</p>
 *
 * @author Ocelot
 * @see Layout
 */
public class SpinnerComponent extends StandardComponent
{
    public static final int DEFAULT_COLOR = 0xFF2F2F2F;
    public static final int SIZE = 12;
    public static final int MAX_PROGRESS = 31;

    private float x;
    private float y;
    private int color;
    private boolean visible;

    private int progress;
    private boolean paused;

    public SpinnerComponent(float x, float y)
    {
        this.createSyncHelper();
        this.x = x;
        this.y = y;
        this.color = DEFAULT_COLOR;
        this.visible = true;
    }

    private void createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("color", nbt -> nbt.putInt("color", this.color), nbt -> this.color = nbt.getInt("color"));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));

            syncHelper.addSerializer("progress", nbt -> nbt.putInt("progress", this.progress), nbt -> this.progress = nbt.getInt("progress"));
            syncHelper.addSerializer("paused", nbt -> nbt.putBoolean("paused", this.paused), nbt -> this.paused = nbt.getBoolean("paused"));
        }
        this.setClientSerializer(syncHelper);
    }

    @Override
    public void update()
    {
        if (!this.paused && this.visible)
        {
            if (this.progress >= MAX_PROGRESS)
            {
                this.progress = 0;
            }
            this.progress++;
        }
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            renderProgress(posX + this.x, posY + this.y, this.color, 0xFFFFFFFF, this.progress);
        }
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
        return SIZE;
    }

    @Override
    public int getHeight()
    {
        return SIZE;
    }

    /**
     * @return The color of this spinner
     */
    public int getColor()
    {
        return color;
    }

    /**
     * @return Whether or not this component can be seen
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * @return The current progression value
     */
    public int getProgress()
    {
        return progress;
    }

    /**
     * @return Whether or not the progression of the spinner is paused
     */
    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public SpinnerComponent setX(int x)
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
    public SpinnerComponent setY(int y)
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
    public SpinnerComponent setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.getClientSerializer().markDirty("x");
        this.getClientSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the color of this component to the specified value.
     *
     * @param color The new color of this component
     */
    public SpinnerComponent setColor(int color)
    {
        this.color = color;
        this.getClientSerializer().markDirty("color");
        return this;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public SpinnerComponent setVisible(boolean visible)
    {
        this.visible = visible;
        this.getClientSerializer().markDirty("visible");
        return this;
    }

    /**
     * Sets whether or not the progression of the spinner is paused
     *
     * @param paused Whether or not this is paused
     */
    public SpinnerComponent setPaused(boolean paused)
    {
        this.paused = paused;
        this.getClientSerializer().markDirty("progress");
        this.getClientSerializer().markDirty("paused");
        return this;
    }

    /**
     * Renders a spinning loading bar at the specified position.
     *
     * @param x               The x position of the bar
     * @param y               The y position of the bar
     * @param backgroundColor The color of the background
     * @param color           The color of the loading portion
     * @param progress        The current progress of the spinner
     */
    public static void renderProgress(float x, float y, int backgroundColor, int color, int progress)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.COMPONENTS_LOCATION);
        RenderUtil.glColor(backgroundColor);
        RenderUtil.drawRectWithTexture(x, y, 0, 0, SIZE, SIZE, SIZE, SIZE, 256, 256);
        RenderUtil.glColor(color);
        RenderUtil.drawRectWithTexture(x, y, (progress % 8) * SIZE, (int) (1 + progress / 8f) * SIZE, SIZE, SIZE, SIZE, SIZE, 256, 256);
        GlStateManager.color4f(1, 1, 1, 1);
    }
}
