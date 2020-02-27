package com.ocelot.opendevices.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>Allows the rendering of tooltips while bypassing {@link Screen}.</p>
 * <p>Mainly used in the API to allow the rendering of tooltips without the overhead of screen.</p>
 *
 * @author Ocelot
 * @see Screen
 */
@OnlyIn(Dist.CLIENT)
public interface TooltipRenderer
{
    /**
     * Renders the tooltip for the specified {@link ItemStack}.
     *
     * @param stack The stack to render the tooltip of
     * @param posX  The x position to render the tooltip at
     * @param posY  The t position to render the tooltip at
     */
    default void renderTooltip(ItemStack stack, int posX, int posY)
    {
        renderTooltip(stack, posX, posY, Minecraft.getInstance().fontRenderer);
    }

    /**
     * Renders the tooltip for the specified {@link ItemStack}.
     *
     * @param stack        The stack to render the tooltip of
     * @param posX         The x position to render the tooltip at
     * @param posY         The t position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text if the stack provides no custom font renderer
     */
    default void renderTooltip(ItemStack stack, int posX, int posY, FontRenderer fontRenderer)
    {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        this.renderTooltip(this.getTooltipFromItem(stack), posX, posY, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    /**
     * Renders a tooltip using the specified string.
     *
     * @param tooltip The string to render on the tooltip
     * @param posX    The x position to render the tooltip at
     * @param posY    The y position to render the tooltip at
     */
    void renderTooltip(String tooltip, int posX, int posY);

    /**
     * Renders a tooltip using the specified string.
     *
     * @param tooltip      The string to render on the tooltip
     * @param posX         The x position to render the tooltip at
     * @param posY         The y position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text
     */
    void renderTooltip(String tooltip, int posX, int posY, FontRenderer fontRenderer);

    /**
     * Renders a tooltip using the specified strings.
     *
     * @param tooltip The strings to render on the tooltip
     * @param posX    The x position to render the tooltip at
     * @param posY    The y position to render the tooltip at
     */
    void renderTooltip(List<String> tooltip, int posX, int posY);

    /**
     * Renders a tooltip using the specified strings.
     *
     * @param tooltip      The strings to render on the tooltip
     * @param posX         The x position to render the tooltip at
     * @param posY         The y position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text
     */
    void renderTooltip(List<String> tooltip, int posX, int posY, FontRenderer fontRenderer);

    /**
     * Renders the tooltip for the specified text component.
     *
     * @param textComponent The component to render the tooltip for
     * @param posX          The x position to render the tooltip at
     * @param posY          The y position to render the tooltip at
     */
    void renderComponentHoverEffect(@Nullable ITextComponent textComponent, int posX, int posY);

    /**
     * Collects the tooltip information from the specified {@link ItemStack}.
     *
     * @param stack The stack to get the tooltip information from
     * @return The lines of tooltip information
     */
    List<String> getTooltipFromItem(ItemStack stack);
}
