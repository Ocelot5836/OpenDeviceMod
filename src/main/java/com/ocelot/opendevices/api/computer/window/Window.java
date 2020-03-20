package com.ocelot.opendevices.api.computer.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.desktop.Desktop;
import com.ocelot.opendevices.api.util.RenderUtil;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>The most abstract form of a Window. This contains all the required functionality for the API.</p>
 * <p>A window is a box that can display onto a {@link Computer}. The window has the ability to be moved and render it's content.</p>
 *
 * @author Ocelot
 * @see Desktop
 */
public interface Window
{
    /**
     * @return The laptop this window is opened inside of
     */
    Computer getComputer();

    /**
     * @return The id of the running this window
     */
    UUID getProcessId();

    /**
     * @return The id of this window. Used for Client/Server synchronization
     */
    UUID getId();

    /**
     * @return The title of the window
     */
    String getTitle();

    /**
     * @return The x position of this window
     */
    float getX();

    /**
     * @return The y position of this window
     */
    float getY();

    /**
     * @return The last x position of this window
     */
    float getLastX();

    /**
     * @return The last y position of this window
     */
    float getLastY();

    /**
     * Interpolates the x position from the last x to the current x.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The interpolated x position
     */
    default float getInterpolatedX(float partialTicks)
    {
        return this.getLastX() + (this.getX() - this.getLastX()) * partialTicks;
    }

    /**
     * Interpolates the y position from the last y to the current y.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The interpolated y position
     */
    default float getInterpolatedY(float partialTicks)
    {
        return this.getLastY() + (this.getY() - this.getLastY()) * partialTicks;
    }

    /**
     * @return The x size of this window including the borders
     */
    int getWidth();

    /**
     * @return The y size of this window including the borders
     */
    int getHeight();

    /**
     * @return The icon of this window or null for no custom icon
     */
    @Nullable
    ResourceLocation getIcon();

    /**
     * Checks to see if the provided x and y is within this window.
     *
     * @param x            The x position to check
     * @param y            The y position to check
     * @param partialTicks The percentage from last tick to this tick
     * @return Whether or not that point is within this box
     */
    default boolean isWithin(double x, double y, float partialTicks)
    {
        float windowX = this.getInterpolatedX(partialTicks);
        float windowY = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(x, y, windowX, windowY, windowX + this.getWidth(), windowY + this.getHeight());
    }

    /**
     * Checks to see if the provided x and y is within the content of this window.
     *
     * @param x            The x position to check
     * @param y            The y position to check
     * @param partialTicks The percentage from last tick to this tick
     * @return Whether or not that point is within this box
     */
    default boolean isWithinContent(double x, double y, float partialTicks)
    {
        float windowX = this.getInterpolatedX(partialTicks);
        float windowY = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(x, y, windowX + 1, windowY + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 1, windowX + this.getWidth() - 1, windowY + this.getHeight() - 1);
    }

    /**
     * Checks to see if the provided x and y is within the bar of this window.
     *
     * @param x            The x position to check
     * @param y            The y position to check
     * @param partialTicks The percentage from last tick to this tick
     * @return Whether or not that point is within this box
     */
    default boolean isWithinWindowBar(double x, double y, float partialTicks)
    {
        float windowX = this.getInterpolatedX(partialTicks);
        float windowY = this.getInterpolatedY(partialTicks);
        return RenderUtil.isMouseInside(x, y, windowX, windowY + 1, windowX + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1, windowY + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT);
    }

    /**
     * Checks to see if the provided x and y is within the button of this window.
     *
     * @param x            The x position to check
     * @param y            The y position to check
     * @param partialTicks The percentage from last tick to this tick
     * @return Whether or not that point is within this box
     */
    default boolean isWithinButton(double x, double y, float partialTicks)
    {
        float windowX = this.getInterpolatedX(partialTicks) + this.getWidth() - DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE - 1;
        float windowY = this.getInterpolatedY(partialTicks) + 1;
        return RenderUtil.isMouseInside(x, y, windowX, windowY, windowX + DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE, windowY + DeviceConstants.LAPTOP_WINDOW_BUTTON_SIZE);
    }
}
