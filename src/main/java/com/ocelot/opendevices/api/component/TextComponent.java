package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.handler.ComponentClickListener;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Allows the addition of {@link ITextComponent} lines of text to a {@link Layout}.</p>
 * <p><i>Note: {@link ClickEvent.Action} will be completely ignored in favor for {@link ComponentClickListener}.</i></p>
 *
 * @author Ocelot
 * @see ITextComponent
 * @see Layout
 */
public class TextComponent extends StandardComponent
{
    private float x;
    private float y;
    private int maxWidth;

    private FontRenderer fontRenderer;
    private ResourceLocation fontRendererLocation;
    private List<ITextComponent> text;
    private long tooltipDelay;
    private boolean renderShadow;
    private boolean visible;

    private List<Line> lines;
    private long lastTooltip;
    private int width;
    private ComponentClickListener<ITextComponent> clickListener;

    public TextComponent(float x, float y, ResourceLocation fontRenderer, ITextComponent... texts)
    {
        this(x, y, -1, fontRenderer, Arrays.asList(texts));
    }

    public TextComponent(float x, float y, int maxWidth, ResourceLocation fontRenderer, ITextComponent... texts)
    {
        this(x, y, maxWidth, fontRenderer, Arrays.asList(texts));
    }

    public TextComponent(float x, float y, ResourceLocation fontRenderer, Collection<ITextComponent> texts)
    {
        this(x, y, -1, fontRenderer, texts);
    }

    public TextComponent(float x, float y, int maxWidth, ResourceLocation fontRenderer, Collection<ITextComponent> texts)
    {
        this.createSyncHelper();
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;

        this.text = new ArrayList<>();
        this.lines = new ArrayList<>();

        this.setFontRenderer(fontRenderer);
        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.visible = true;

        this.lastTooltip = Long.MAX_VALUE;
        texts.forEach(this::addLine);
    }

    private void createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("maxWidth", nbt -> nbt.putInt("maxWidth", this.maxWidth), nbt -> this.maxWidth = nbt.getInt("maxWidth"));

            syncHelper.addSerializer("fontRenderer", nbt -> nbt.putString("fontRenderer", this.fontRendererLocation.toString()), nbt -> this.fontRendererLocation = new ResourceLocation(nbt.getString("fontRenderer")));
            syncHelper.addSerializer("text", this::serializeText, this::deserializeText);
            syncHelper.addSerializer("tooltipDelay", nbt -> nbt.putLong("tooltipDelay", this.tooltipDelay), nbt -> this.tooltipDelay = nbt.getLong("tooltipDelay"));
            syncHelper.addSerializer("renderShadow", nbt -> nbt.putBoolean("renderShadow", this.renderShadow), nbt -> this.renderShadow = nbt.getBoolean("renderShadow"));
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));
        }
        this.setClientSerializer(syncHelper);
    }

    private void serializeText(CompoundNBT nbt)
    {
        ListNBT textList = new ListNBT();
        this.text.forEach(text -> textList.add(new StringNBT(new String(Base64.getEncoder().encode(ITextComponent.Serializer.toJson(text).getBytes())))));
        nbt.put("text", textList);
    }

    private void deserializeText(CompoundNBT nbt)
    {
        this.text.clear();
        ListNBT textList = nbt.getList("text", Constants.NBT.TAG_STRING);
        for (int i = 0; i < textList.size(); i++)
        {
            this.text.add(ITextComponent.Serializer.fromJson(new String(Base64.getDecoder().decode(textList.getString(i)))));
        }
        this.rebuildText();
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
        if (!this.visible || !this.isHovered(mouseX, mouseY))
            return null;

        for (int i = 0; i < this.lines.size(); i++)
        {
            Line line = this.lines.get(i);
            int yOffset = i * this.fontRenderer.FONT_HEIGHT;
            if (RenderUtil.isMouseInside(mouseX, mouseY, this.x, this.y + yOffset, this.x + line.width, this.y + yOffset + this.fontRenderer.FONT_HEIGHT))
            {
                double x = this.x;
                for (ITextComponent lineComponent : line.textComponent)
                {
                    if (lineComponent instanceof StringTextComponent)
                    {
                        x += this.fontRenderer.getStringWidth(((StringTextComponent) lineComponent).getText());
                        if (x > mouseX)
                        {
                            return lineComponent;
                        }
                    }
                }
                break;
            }
        }

        return null;
    }

    /**
     * Plays the sound when this component is pressed.
     *
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param mouseButton The button pressed
     */
    protected void playPressSound(double mouseX, double mouseY, int mouseButton)
    {
    }

    /**
     * Adds the specified text to this component. In order to call this during a tick, use {@link Computer#execute(Runnable)}!
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
    public void render(float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            for (int i = 0; i < this.lines.size(); i++)
            {
                Line line = this.lines.get(i);
                int yOffset = i * this.fontRenderer.FONT_HEIGHT;
                if (this.renderShadow)
                {
                    this.fontRenderer.drawStringWithShadow(line.text, posX + this.x, posY + this.y + yOffset, 0xffffffff);
                }
                else
                {
                    this.fontRenderer.drawString(line.text, posX + this.x, posY + this.y + yOffset, 0xffffffff);
                }
            }
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            if (this.isHovered(mouseX - posX, mouseY - posY))
            {
                if (this.lastTooltip == Long.MAX_VALUE)
                    this.lastTooltip = System.currentTimeMillis();
                if (System.currentTimeMillis() - this.lastTooltip >= this.tooltipDelay)
                    renderer.renderComponentHoverEffect(this.getHoveredText(mouseX - posX, mouseY - posY), mouseX, mouseY);
            }
            else
            {
                this.lastTooltip = Long.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        ITextComponent hoveredText = this.getHoveredText(mouseX, mouseY);
        if (this.visible && hoveredText != null)
        {
            if (this.clickListener != null)
            {
                if (this.clickListener.handle(hoveredText, mouseX, mouseY, mouseButton))
                {
                    this.playPressSound(mouseX, mouseY, mouseButton);
                    return true;
                }
            }
            else
            {
                this.playPressSound(mouseX, mouseY, mouseButton);
                return true;
            }
        }
        return false;
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
     * @return The text this component displays
     */
    public ITextComponent[] getText()
    {
        return this.text.toArray(new ITextComponent[0]);
    }

    /**
     * @return The time it takes for tooltips to begin rendering in ms
     */
    public long getTooltipDelay()
    {
        return tooltipDelay;
    }

    /**
     * @return Whether or not a shadow will render behind the text
     */
    public boolean shouldRenderShadow()
    {
        return renderShadow;
    }

    /**
     * @return Whether or not this text can be seen and interacted with
     */
    public boolean isVisible()
    {
        return visible;
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
    public TextComponent setX(float x)
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
    public TextComponent setY(float y)
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
    public TextComponent setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.getClientSerializer().markDirty("x");
        this.getClientSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the maximum width of a line before being split. In order to call this during a tick, use {@link Executor#execute(Runnable)}!
     *
     * @param maxWidth The new maximum width
     */
    public TextComponent setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.rebuildText();
        this.getClientSerializer().markDirty("maxWidth");
        return this;
    }

    /**
     * Sets the font renderer used for rendering text. In order to call this during a tick, use {@link Executor#execute(Runnable)}!
     *
     * @param fontRenderer The new font renderer
     */
    public TextComponent setFontRenderer(ResourceLocation fontRenderer)
    {
        this.fontRendererLocation = fontRenderer;
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
        this.rebuildText();
        this.getClientSerializer().markDirty("fontRenderer");
        return this;
    }

    /**
     * Sets the text displayed on the button to the specified information. In order to call this during a tick, use {@link Executor#execute(Runnable)}!
     *
     * @param text The new text to display
     */
    public TextComponent setText(ITextComponent... text)
    {
        this.setText(Arrays.asList(text));
        return this;
    }

    /**
     * Sets the text displayed on the button to the specified information. In order to call this during a tick, use {@link Executor#execute(Runnable)}!
     *
     * @param text The new text to display
     */
    public TextComponent setText(@Nullable List<ITextComponent> text)
    {
        this.text.clear();
        if (text != null)
            text.forEach(this::addLine);
        this.rebuildText();
        this.getClientSerializer().markDirty("text");
        return this;
    }

    /**
     * Sets the amount of time in the specified time unit it takes for a tooltip to begin rendering.
     *
     * @param unit         The time unit to use
     * @param tooltipDelay The time it takes for tooltips to begin rendering
     */
    public TextComponent setTooltipDelay(TimeUnit unit, long tooltipDelay)
    {
        this.tooltipDelay = Math.max(0, unit.toMillis(tooltipDelay));
        this.getClientSerializer().markDirty("tooltipDelay");
        return this;
    }

    /**
     * Sets whether or not a shadow will render behind this text.
     *
     * @param renderShadow Whether or not to render a shadow
     */
    public TextComponent setRenderShadow(boolean renderShadow)
    {
        this.renderShadow = renderShadow;
        this.getClientSerializer().markDirty("renderShadow");
        return this;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public TextComponent setVisible(boolean visible)
    {
        this.visible = visible;
        this.getClientSerializer().markDirty("visible");
        return this;
    }

    /**
     * Sets the click listener for this text.
     *
     * @param clickListener The new click listener to use or null to remove the listener
     */
    public TextComponent setClickListener(@Nullable ComponentClickListener<ITextComponent> clickListener)
    {
        this.clickListener = clickListener;
        return this;
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
