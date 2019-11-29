package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.handler.ComponentClickListener;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Allows the addition of {@link ITextComponent} lines of text to be added to a {@link Layout}.</p>
 * <p><i>Note: {@link ClickEvent.Action} will be completely ignored in favor for {@link ComponentClickListener}.</i></p>
 *
 * @author Ocelot
 * @see ITextComponent
 * @see Layout
 */
public class TextComponent extends BasicComponent
{
    private int x;
    private int y;
    private int maxWidth;
    private List<ITextComponent> text;
    private List<Line> lines;
    private ResourceLocation fontRendererLocation;
    private FontRenderer fontRenderer;
    private int width;

    private ComponentClickListener<ITextComponent> clickListener;

    public TextComponent()
    {
        this.text = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public TextComponent(int x, int y, ResourceLocation fontRenderer, ITextComponent... texts)
    {
        this(x, y, -1, fontRenderer, texts);
    }

    public TextComponent(int x, int y, int maxWidth, ResourceLocation fontRenderer, ITextComponent... texts)
    {
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;
        this.text = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.setFontRenderer(fontRenderer);
        Arrays.stream(texts).forEach(this::addLine);
    }

    private void rebuildText()
    {
        this.width = 0;
        this.lines.clear();
        this.text.forEach(this::addLineInternal);
    }

    private void addLineInternal(ITextComponent text)
    {
        List<ITextComponent> texts = this.maxWidth == -1 ? Collections.singletonList(text) : RenderComponentsUtil.splitText(text, this.maxWidth, this.fontRenderer, true, true);
        for (ITextComponent textComponent : texts)
        {
            Line line = new Line(this.fontRenderer, textComponent);
            this.lines.add(line);

            if (line.width > this.width)
            {
                this.width = line.width;
            }
        }
    }

    private ITextComponent getHoveredText(double mouseX, double mouseY)
    {
        if (!this.isHovered(mouseX, mouseY))
            return null;

        for (int i = 0; i < this.lines.size(); i++)
        {
            Line line = this.lines.get(i);
            int yOffset = i * this.fontRenderer.FONT_HEIGHT;
            if (RenderUtil.isMouseInside(mouseX, mouseY, this.getWindowX() + this.x, this.getWindowY() + this.y + yOffset, this.getWindowX() + this.x + line.width, this.getWindowY() + this.y + yOffset + this.fontRenderer.FONT_HEIGHT))
            {
                int x = 0;
                for (ITextComponent lineComponent : line.textComponent.getSiblings())
                {
                    x += this.fontRenderer.getStringWidth(lineComponent.getFormattedText());
                    if (mouseX > x)
                    {
                        return lineComponent;
                    }
                }
                break;
            }
        }

        return null;
    }

    /**
     * Adds the specified text to this component. In order to call this during a tick, use {@link Laptop#execute(Runnable)}!
     *
     * @param text The text to add
     */
    public void addLine(ITextComponent text)
    {
        this.text.add(text);
        this.addLineInternal(text);
    }

    @Override
    public void update()
    {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < this.lines.size(); i++)
        {
            Line line = this.lines.get(i);
            int yOffset = i * this.fontRenderer.FONT_HEIGHT;
            this.fontRenderer.drawString(line.text, this.getWindowX() + this.x, this.getWindowY() + this.y + yOffset, 0xffffffff);
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        renderer.renderComponentHoverEffect(this.getHoveredText(mouseX, mouseY), mouseX, mouseY);
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        if (this.clickListener != null)
        {
            ITextComponent hoveredText = this.getHoveredText(mouseX, mouseY);
            if (hoveredText != null)
            {
                this.clickListener.handle(hoveredText, mouseX, mouseY, mouseButton);
            }
        }
        return super.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.lines.size() * this.fontRenderer.FONT_HEIGHT;
    }

    /**
     * @return The maximum width of a line before being split
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * @return The font renderer used for rendering text
     */
    public FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    /**
     * @return The set click listener or null if there is no click listener
     */
    @Nullable
    public ComponentClickListener<ITextComponent> getClickListener()
    {
        return clickListener;
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Sets the y position of this component to the specified value.
     *
     * @param y The new y position
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * Sets the position of this component to the specified values.
     *
     * @param x The new x position
     * @param y The new y position
     */
    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the maximum width of a line before being split. In order to call this during a tick, use {@link Laptop#execute(Runnable)}!
     *
     * @param maxWidth The new maximum width
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.rebuildText();
    }

    /**
     * Sets the font renderer used for rendering text. In order to call this during a tick, use {@link Laptop#execute(Runnable)}!
     *
     * @param fontRenderer The new font renderer
     */
    public void setFontRenderer(ResourceLocation fontRenderer)
    {
        this.fontRendererLocation = fontRenderer;
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
        this.rebuildText();
    }

    /**
     * Sets the click listener for this text.
     *
     * @param clickListener The new click listener to use or null to remove the listener
     */
    public void setClickListener(@Nullable ComponentClickListener<ITextComponent> clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putString("fontRenderer", this.fontRendererLocation.toString());
        nbt.putInt("maxWidth", this.maxWidth);

        ListNBT textList = new ListNBT();
        this.text.forEach(text -> textList.add(new StringNBT(ITextComponent.Serializer.toJson(text))));
        nbt.put("text", textList);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.setFontRenderer(new ResourceLocation(nbt.getString("fontRenderer")));
        this.maxWidth = nbt.getInt("maxWidth");

        ListNBT textList = nbt.getList("text", Constants.NBT.TAG_STRING);
        for (int i = 0; i < textList.size(); i++)
        {
            this.addLine(ITextComponent.Serializer.fromJson(textList.getString(i)));
        }
    }

    private static class Line
    {
        private FontRenderer fontRenderer;
        private ITextComponent textComponent;
        private String text;
        private int width;

        private Line(FontRenderer fontRenderer, ITextComponent text)
        {
            this.fontRenderer = fontRenderer;
            this.setText(text);
        }

        public void setText(@Nullable ITextComponent text)
        {
            this.textComponent = text;
            this.text = text == null ? "null" : text.getFormattedText();
            this.width = this.fontRenderer.getStringWidth(this.text);
        }
    }
}
