package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

/**
 * <p>Components are the building blocks of the rendering API in windows by default. These don't have to be used but they allow an in-built application engine.</p>
 *
 * @author Ocelot
 */
public interface Component extends INBTSerializable<CompoundNBT>
{
    /**
     * Marks this component as needing to be synced.
     */
    default void markDirty()
    {
        this.setDirty(true);
    }

    /**
     * Called 20 times per second to update any logic.
     */
    void update();

    /**
     * Renders the contents of this component.
     *
     * @param posX         The x position of the window
     * @param posY         The y position of the window
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    void render(float posX, float posY, int mouseX, int mouseY, float partialTicks);

    /**
     * Renders the overlay contents of this component.
     *
     * @param renderer     The rendering instance used for rendering tooltips
     * @param posX         The x position of the window
     * @param posY         The y position of the window
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks);

    /**
     * Called when a key is pressed on the keyboard.
     *
     * @param keyCode  The id of the key released or {@link GLFW#GLFW_KEY_UNKNOWN} if the key does not have a key token
     * @param scanCode A unique id for each key regardless of key tokens, but is platform-specific
     * @param mods     The modifier key flags
     * @return Whether or not this event was consumed
     */
    default boolean onKeyPressed(int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Called when a key is released on the keyboard.
     *
     * @param keyCode  The id of the key released or {@link GLFW#GLFW_KEY_UNKNOWN} if the key does not have a key token
     * @param scanCode A unique id for each key regardless of key tokens, but is platform-specific
     * @param mods     The modifier key flags
     * @return Whether or not this event was consumed
     */
    default boolean onKeyReleased(int keyCode, int scanCode, int mods)
    {
        return false;
    }

    /**
     * Called when a button is pressed on the mouse.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed on the mouse
     * @return Whether or not this event was consumed
     */
    default boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    /**
     * Called when a button is released on the mouse.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed on the mouse
     * @return Whether or not this event was consumed
     */
    default boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    /**
     * Called when the mouse wheel of the mouse is scrolled.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param amount The amount the mouse wheel was scrolled
     * @return Whether or not this event was consumed
     */
    default boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        return false;
    }

    /**
     * Called when the mouse is moved.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    default void onMouseMoved(double mouseX, double mouseY)
    {
    }

    /**
     * Called when the mouse is moved while a button is held.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed on the mouse while moving
     * @param deltaX      The amount in the x direction the mouse has moved
     * @param deltaY      The amount in the y direction the mouse has moved
     * @return Whether or not this event was consumed
     */
    default boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        return false;
    }

    /**
     * Called when the layout becomes focused.
     */
    default void onGainFocus()
    {
    }

    /**
     * Called when the layout loses focused.
     */
    default void onLostFocus()
    {
    }

    /**
     * Called when the layout is closed.
     */
    default void onClose()
    {
    }

    /**
     * Called right after the layout holding this component is set to the current one.
     */
    default void onLayoutLoad()
    {
    }

    /**
     * Called right before the layout holding this component is changed to a new one.
     */
    default void onLayoutUnload()
    {
    }

    /**
     * Checks to see if this component is hovered or not.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @return Whether or not this component is hovered at the specified mouse position
     */
    default boolean isHovered(double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
    }

    /**
     * @return The x position of this component
     */
    float getX();

    /**
     * @return The y position of this component
     */
    float getY();

    /**
     * @return The x size of this component
     */
    int getWidth();

    /**
     * @return The y size of this component
     */
    int getHeight();

    /**
     * @return Whether or not this component needs to be synced
     */
    boolean isDirty();

    /**
     * Marks this component as needing to be synced or not.
     * @param dirty Whether or not this component should be synced
     */
    void setDirty(boolean dirty);
}
