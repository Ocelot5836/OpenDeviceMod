package com.ocelot.opendevices.api.component;

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
 * <p><i>Note: {@link ClickEvent} will <b>ONLY</b> run the supplied listener and will not respect {@link ClickEvent.Action}.</i></p>
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
    private ResourceLocation fontRendererLocation;
    private FontRenderer fontRenderer;
    private List<Line> lines;
    private int width;

    public TextComponent()
    {
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
        this.fontRendererLocation = fontRenderer;
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
        this.lines = new ArrayList<>();
        Arrays.stream(texts).forEach(this::addLine);
    }

    /**
     * Adds the specified text to this component. Make sure not to call this during a tick!
     *
     * @param text The text to add
     */
    public void addLine(ITextComponent text)
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
        if (this.isHovered(mouseX, mouseY))
        {
            for (int i = 0; i < this.lines.size(); i++)
            {
                Line line = this.lines.get(i);
                int yOffset = i * this.fontRenderer.FONT_HEIGHT;
                if (RenderUtil.isMouseInside(mouseX, mouseY, this.getWindowX() + this.x, this.getWindowY() + this.y + yOffset, this.getWindowX() + this.x + line.width, this.getWindowY() + this.y + yOffset + this.fontRenderer.FONT_HEIGHT))
                {
                    int x = 0;
                    for (ITextComponent lineComponent : line.textComponent)
                    {
                        x += this.fontRenderer.getStringWidth(lineComponent.getFormattedText());
                        if (mouseX > x)
                        {
                            renderer.renderComponentHoverEffect(lineComponent, mouseX, mouseY);
                        }
                    }
                    break;
                }
            }
        }
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

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setFontRenderer(ResourceLocation fontRenderer)
    {
        this.fontRendererLocation = fontRenderer;
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putString("fontRenderer", this.fontRendererLocation.toString());
        nbt.putInt("maxWidth", this.maxWidth);

        ListNBT text = new ListNBT();
        this.lines.forEach(line -> text.add(new StringNBT(line.toJson())));
        nbt.put("text", text);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.setFontRenderer(new ResourceLocation(nbt.getString("fontRenderer")));
        this.maxWidth = nbt.getInt("maxWidth");

        ListNBT text = nbt.getList("text", Constants.NBT.TAG_STRING);
        for (int i = 0; i < text.size(); i++)
        {
            Line line = new Line(this.fontRenderer, text.getString(i));
            this.lines.add(line);
            if (line.width > this.width)
            {
                this.width = line.width;
            }
        }
    }

    private static class Line
    {
        private FontRenderer fontRenderer;
        private ITextComponent textComponent;
        private String text;
        private int width;

        private Line(FontRenderer fontRenderer, String json)
        {
            this.fontRenderer = fontRenderer;
            this.setText(ITextComponent.Serializer.fromJson(json));
        }

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

        public String toJson()
        {
            return ITextComponent.Serializer.toJson(this.textComponent);
        }
    }
}
