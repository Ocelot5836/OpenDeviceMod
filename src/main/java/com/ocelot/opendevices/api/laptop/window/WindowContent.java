package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.window.application.Application;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>The most abstract form of any content that can be found within a window.</p>
 * <p>Mainly used for {@link Application} and Dialog.</p>
 *
 * @author Ocelot
 * @see Window
 */
public interface WindowContent
{
    /**
     * Called 20 times per second to update any logic.
     */
    void update();

    /**
     * Renders the content to the screen.
     *
     * @param x            The x position of the window
     * @param y            The y position of the window
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last update and this update
     */
    void render(float x, float y, int mouseX, int mouseY, float partialTicks);

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
     * Saves the current state to NBT.
     *
     * @param nbt The tag to fill with data
     */
    void saveState(CompoundNBT nbt);

    /**
     * Loads the current state from NBT.
     *
     * @param nbt The tag containing data
     */
    void loadState(CompoundNBT nbt);

    /**
     * Called when the window displaying this content is now focused.
     */
    void onGainFocus();

    /**
     * Called when the window displaying this content is no longer focused.
     */
    void onLostFocus();

    /**
     * Called when the window displaying this content is closed.
     */
    void onClose();

    /**
     * @return The title of the window displaying this content or null for no title
     */
    @Nullable
    String getTitle();

    /**
     * @return The icon of the window displaying this content or null for no icon
     */
    @Nullable
    TextureAtlasSprite getIconSprite();

    /**
     * @return The location of the map containing the icon of the window displaying this content. Only used if {@link #getIconSprite()} is not null
     */
    ResourceLocation getIconMapLocation();

    /**
     * @return The window this content is displayed in
     */
    Window getWindow();
}
