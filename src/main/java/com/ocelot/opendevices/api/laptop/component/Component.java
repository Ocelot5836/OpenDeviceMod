package com.ocelot.opendevices.api.laptop.component;

import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <p>Components are the building blocks of the rendering API in windows by default. Everything supported in the default API must implement this class in order to be used.</p>
 * <p>Mainly used in {@link Layout} to specify components for locations.</p>
 *
 * @author Ocelot
 * @see Layout
 */
@OnlyIn(Dist.CLIENT)
public interface Component
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
    float getWidth();

    /**
     * @return The y size of this component
     */
    float getHeight();

    /**
     * Updates the position of the screen to the provided values.
     *
     * @param windowX The x position of the window
     * @param windowY The y position of the window
     */
    void setWindowPosition(float windowX, float windowY);
}
