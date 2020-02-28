package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * TODO update methods
 * <p>Components are the building blocks of the rendering API in windows by default. Everything supported in the default API must implement this class in order to be used.</p>
 *
 * @author Ocelot
 */
public interface Component extends INBTSerializable<CompoundNBT>
{
    /**
     * Called 20 times per second to update any logic.
     */
    void update();

    /**
     * Renders the actual component to the screen.
     *
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last update and this update
     */
    void render(int mouseX, int mouseY, float partialTicks);

    /**
     * Renders overlaying component data to the screen.
     *
     * @param renderer     The renderer the tooltips are being drawn into
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last update and this update
     */
    void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks);

    /**
     * Called when the mouse is pressed.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed
     * @return Whether or not the action was handled
     */
    boolean onMousePressed(double mouseX, double mouseY, int mouseButton);

    /**
     * Called when the mouse is released.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button released
     * @return Whether or not the action was handled
     */
    boolean onMouseReleased(double mouseX, double mouseY, int mouseButton);

    /**
     * Called when the mouse wheel is scrolled.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param amount The amount scrolled
     * @return Whether or not the action was handled
     */
    boolean onMouseScrolled(double mouseX, double mouseY, double amount);

    /**
     * Called when the mouse is moved.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    void onMouseMoved(double mouseX, double mouseY);

    /**
     * Called when the mouse is moved while being pressed.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button being held
     * @param deltaX      The x speed of the mouse
     * @param deltaY      The y speed of the mouse
     * @return Whether or not the action was handled
     */
    boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY);

    /**
     * Called when a key is pressed.
     *
     * @param keyCode The id of the key pressed
     * @return Whether or not the action was handled
     */
    boolean onKeyPressed(int keyCode);

    /**
     * Called when a key is released.
     *
     * @param keyCode The id of the key released
     * @return Whether or not the action was handled
     */
    boolean onKeyReleased(int keyCode);

    /**
     * Called when the layout is now focused.
     */
    void onGainFocus();

    /**
     * Called when the layout is no longer focused.
     */
    void onLostFocus();

    /**
     * Called when the layout is closed.
     */
    void onClose();

    /**
     * Called right after the layout holding this component is set to the current one.
     */
    void onLayoutLoad();

    /**
     * Called right before the layout holding this component is changed to a new one.
     */
    void onLayoutUnload();

    /**
     * Checks to see if this component is hovered or not.
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @return Whether or not this component is hovered at the specified mouse position
     */
    default boolean isHovered(double mouseX, double mouseY)
    {
        return RenderUtil.isMouseInside(mouseX, mouseY, this.getX(), this.getY(), this.getMaxX(), this.getMaxY());
    }

    /**
     * @return The left x position of this component
     */
    int getX();

    /**
     * @return The top y position of this component
     */
    int getY();

    /**
     * @return The right x position of this component
     */
    default int getMaxX()
    {
        return this.getX() + this.getWidth();
    }

    /**
     * @return The bottom y position of this component
     */
    default int getMaxY()
    {
        return this.getY() + this.getHeight();
    }

    /**
     * @return The x size of this component
     */
    int getWidth();

    /**
     * @return The y size of this component
     */
    int getHeight();

    /**
     * @return The window this component is in. This reference is null during class construction
     * @deprecated Find a way to not require the window instance. Components don't need to be in windows!
     */
    Window getWindow();

    /**
     * Sets the window instance for this component
     *
     * @param window The new window
     */
    void setWindow(Window window);

    /**
     * Registers a new component.
     *
     * @author Ocelot
     * @see Component
     */
    @Target(ElementType.TYPE)
    @interface Register
    {
        /**
         * @return The name of this component. Should be in the format of <code>modid:componentName</code>
         */
        String value();
    }
}
