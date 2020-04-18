package com.ocelot.opendevices.api.util;

import net.minecraft.client.gui.FontRenderer;

/**
 * <p>Contains methods for drawing text onto the screen.</p>
 *
 * @author Ocelot
 * @see FontRenderer
 */
public class FontHelper
{
    /**
     * Draws the specified string of text.
     *
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param x            The x position of the text
     * @param y            The y position of the text
     * @param color        The color of the text
     * @param shadow       Whether or not to draw a shadow
     */
    public static void drawString(FontRenderer fontRenderer, String text, float x, float y, int color, boolean shadow)
    {
        if (shadow)
            fontRenderer.drawStringWithShadow(text, x, y, color);
        else
            fontRenderer.drawString(text, x, y, color);
    }

    /**
     * Draws the specified string of text to fit within the specified width.
     *
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param x            The x position of the text
     * @param y            The y position of the text
     * @param width        The max width the text can be before trimming occurs
     * @param color        The color of the text
     * @param shadow       Whether or not to draw a shadow
     */
    public static void drawStringClipped(FontRenderer fontRenderer, String text, float x, float y, int width, int color, boolean shadow)
    {
        drawString(fontRenderer, clipStringToWidth(fontRenderer, text, width), x, y, color, shadow);
    }

    /**
     * Clips the provided string to fit within the provided width.
     *
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param width        The max width the text can be before trimming occurs
     * @return The string clipped to the width
     */
    public static String clipStringToWidth(FontRenderer fontRenderer, String text, int width)
    {
        return fontRenderer.getStringWidth(text) > width ? fontRenderer.trimStringToWidth(text, width - fontRenderer.getStringWidth("...")) + "..." : text;
    }

    /**
     * Converts Minecraft world time to a 24 hour format.
     *
     * @param time The current world time
     * @return The formatted time
     */
    public static String timeToString(long time, boolean simple)
    {
        int hours = (int) ((Math.floor(time / 1000.0) + 6) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
        String value = String.format("%02d:%02d", simple ? hours % 12 : hours, minutes);
        if (simple)
            value += " " + (hours / 12 > 0 ? "PM" : "AM");
        return value;
    }
}
