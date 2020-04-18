package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.handler.ClickListener;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.icon.IIcon;
import io.github.ocelot.client.ShapeRenderer;
import io.github.ocelot.client.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL11C.*;

/**
 * <p>Allows the addition of clickable buttons to a {@link Layout}.</p>
 *
 * @author Ocelot
 * @see Layout
 */
public class ButtonComponent extends StandardComponent
{
    public static final int DEFAULT_DISABLED_BUTTON_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_BUTTON_COLOR = 0xFFAAAAAA;
    public static final int DEFAULT_HOVERED_BUTTON_COLOR = 0xFFBDC6FF;
    public static final int DEFAULT_DISABLED_TEXT_COLOR = 0xFFE0E0E0;
    public static final int DEFAULT_TEXT_COLOR = 0xFFA0A0A0;
    public static final int DEFAULT_HOVERED_TEXT_COLOR = 0xFFFFFFA0;

    private float x;
    private float y;
    private int width;
    private int height;
    private int padding;
    private boolean explicitWidth;
    private boolean explicitHeight;

    private FontRenderer fontRenderer;
    private ResourceLocation fontRendererLocation;
    private ITextComponent text;
    private long tooltipDelay;

    private ResourceLocation iconLocation;
    private int iconU;
    private int iconV;
    private int iconWidth;
    private int iconHeight;
    private int iconSourceWidth;
    private int iconSourceHeight;

    private ButtonState state;
    private int disabledButtonColor;
    private int buttonColor;
    private int hoveredButtonColor;
    private int disabledTextColor;
    private int textColor;
    private int hoveredTextColor;

    private String rawText;
    private int textWidth;
    private long lastTooltip;
    private ClickListener clickListener;

    public ButtonComponent(int x, int y)
    {
        this.createSyncHelper();
        this.x = x;
        this.y = y;
        this.padding = 5;
        this.updateSize();
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(Minecraft.DEFAULT_FONT_RENDERER_NAME);
        this.fontRendererLocation = Minecraft.DEFAULT_FONT_RENDERER_NAME;
        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.lastTooltip = Long.MAX_VALUE;

        this.state = ButtonState.VISIBLE;
        this.disabledButtonColor = DEFAULT_DISABLED_BUTTON_COLOR;
        this.buttonColor = DEFAULT_BUTTON_COLOR;
        this.hoveredButtonColor = DEFAULT_HOVERED_BUTTON_COLOR;
        this.disabledTextColor = DEFAULT_DISABLED_TEXT_COLOR;
        this.textColor = DEFAULT_TEXT_COLOR;
        this.hoveredTextColor = DEFAULT_HOVERED_TEXT_COLOR;
    }

    private void createSyncHelper()
    {
        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("x", nbt -> nbt.putFloat("x", this.x), nbt -> this.x = nbt.getFloat("x"));
            syncHelper.addSerializer("y", nbt -> nbt.putFloat("y", this.y), nbt -> this.y = nbt.getFloat("y"));
            syncHelper.addSerializer("width", nbt -> nbt.putInt("width", this.width), nbt -> this.width = nbt.getInt("width"));
            syncHelper.addSerializer("height", nbt -> nbt.putInt("height", this.height), nbt -> this.height = nbt.getInt("height"));

            syncHelper.addSerializer("fontRenderer", nbt -> nbt.putString("fontRenderer", this.fontRendererLocation.toString()), nbt ->
            {
                this.fontRendererLocation = new ResourceLocation(nbt.getString("fontRenderer"));
                this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(this.fontRendererLocation);
            });
            syncHelper.addSerializer("text", nbt -> nbt.putString("text", new String(Base64.getEncoder().encode(ITextComponent.Serializer.toJson(this.text).getBytes()))), nbt -> this.text = ITextComponent.Serializer.fromJson(new String(Base64.getDecoder().decode(nbt.getString("text")))));
            syncHelper.addSerializer("tooltipDelay", nbt -> nbt.putLong("tooltipDelay", this.tooltipDelay), nbt -> this.tooltipDelay = nbt.getLong("tooltipDelay"));

            syncHelper.addSerializer("icon", nbt ->
            {
                CompoundNBT iconNbt = new CompoundNBT();
                if (this.iconLocation != null)
                    iconNbt.putString("location", this.iconLocation.toString());
                iconNbt.putInt("u", this.iconU);
                iconNbt.putInt("v", this.iconV);
                iconNbt.putInt("width", this.iconWidth);
                iconNbt.putInt("height", this.iconHeight);
                iconNbt.putInt("sourceWidth", this.iconSourceWidth);
                iconNbt.putInt("sourceHeight", this.iconSourceHeight);
                nbt.put("icon", iconNbt);
            }, nbt ->
            {
                CompoundNBT iconNbt = nbt.getCompound("icon");
                this.iconLocation = iconNbt.contains("location", Constants.NBT.TAG_STRING) ? new ResourceLocation(iconNbt.getString("location")) : null;
                this.iconU = iconNbt.getInt("u");
                this.iconV = iconNbt.getInt("v");
                this.iconWidth = iconNbt.getInt("width");
                this.iconHeight = iconNbt.getInt("height");
                this.iconSourceWidth = iconNbt.getInt("sourceWidth");
                this.iconSourceHeight = iconNbt.getInt("sourceHeight");
            });

            syncHelper.addSerializer("state", nbt -> nbt.putByte("state", this.state.serialize()), nbt -> this.state = ButtonState.deserialize(nbt.getByte("state")));
            syncHelper.addSerializer("disabledButtonColor", nbt -> nbt.putInt("disabledButtonColor", this.disabledButtonColor), nbt -> this.disabledButtonColor = nbt.getInt("disabledButtonColor"));
            syncHelper.addSerializer("buttonColor", nbt -> nbt.putInt("buttonColor", this.buttonColor), nbt -> this.buttonColor = nbt.getInt("buttonColor"));
            syncHelper.addSerializer("hoveredButtonColor", nbt -> nbt.putInt("hoveredButtonColor", this.hoveredButtonColor), nbt -> this.hoveredButtonColor = nbt.getInt("hoveredButtonColor"));
            syncHelper.addSerializer("disabledTextColor", nbt -> nbt.putInt("disabledTextColor", this.disabledTextColor), nbt -> this.disabledTextColor = nbt.getInt("disabledTextColor"));
            syncHelper.addSerializer("textColor", nbt -> nbt.putInt("textColor", this.textColor), nbt -> this.textColor = nbt.getInt("textColor"));
            syncHelper.addSerializer("hoveredTextColor", nbt -> nbt.putInt("hoveredTextColor", this.hoveredTextColor), nbt -> this.hoveredTextColor = nbt.getInt("hoveredTextColor"));
        }
        this.setValueSerializer(syncHelper);
    }

    private void updateTextCache()
    {
        if (this.text != null)
        {
            this.rawText = this.text.getFormattedText();
            this.textWidth = this.fontRenderer.getStringWidth(this.rawText);
        }
    }

    private void updateSize()
    {
        if (this.explicitWidth && this.explicitHeight)
            return;

        int width = this.padding * 2;
        int height = this.padding * 2;

        if (this.iconLocation != null)
        {
            width += this.iconWidth;
            height += this.iconHeight;
        }

        if (this.text != null)
        {
            width += this.textWidth;
            height = 16;

            if (this.iconLocation != null)
            {
                width += 3;
                height = this.iconHeight + this.padding * 2;
            }
        }

        if (!this.explicitWidth)
        {
            this.width = width;
            this.getValueSerializer().markDirty("width");
        }
        if (!this.explicitHeight)
        {
            this.height = height;
            this.getValueSerializer().markDirty("height");
        }
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
        // TODO make a sound handler that can allow for speakers
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void update()
    {
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        if (this.state != ButtonState.INVISIBLE)
        {
            boolean hovered = main && this.isHovered(mouseX - posX, mouseY - posY);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

            Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.COMPONENTS_LOCATION);
            RenderUtil.glColor(this.disabledButtonColor);
            {
                IVertexBuilder buffer = ShapeRenderer.begin();

                /* Corners */
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x, posY + this.y, 96, 12, 2, 2);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y, 99, 12, 2, 2);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x, posY + this.y + this.height - 2, 96, 15, 2, 2);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y + this.height - 2, 99, 15, 2, 2);

                /* Middles */
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y, 98, 12, this.width - 4, 2, 1, 2);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y + 2, 99, 12, 2, this.height - 4, 2, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y + this.height - 2, 98, 15, this.width - 4, 2, 1, 2);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x, posY + this.y + 2, 96, 14, 2, this.height - 4, 2, 1);

                /* Center */
                if (this.state == ButtonState.DISABLED)
                {
                    ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y + 2, 98, 14, this.width - 4, this.height - 4, 1, 1);
                }

                ShapeRenderer.end();
            }

            RenderUtil.glColor(this.state == ButtonState.DISABLED ? this.disabledButtonColor : hovered ? this.hoveredButtonColor : this.buttonColor);
            if (this.state != ButtonState.DISABLED)
            {
                IVertexBuilder buffer = ShapeRenderer.begin();

                /* Corners */
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 1, posY + this.y + 1, 102, 13, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y + 1, 104, 13, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 1, posY + this.y + this.height - 2, 102, 15, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y + this.height - 2, 104, 15, 1, 1);

                /* Middles */
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y + 1, 103, 13, this.width - 4, 1, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + this.width - 2, posY + this.y + 2, 104, 14, 1, this.height - 4, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y + this.height - 2, 103, 15, this.width - 4, 1, 1, 1);
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 1, posY + this.y + 2, 102, 14, 1, this.height - 4, 1, 1);

                /* Center */
                ShapeRenderer.drawRectWithTexture(buffer, posX + this.x + 2, posY + this.y + 2, 103, 14, this.width - 4, this.height - 4, 1, 1);

                ShapeRenderer.end();
            }
            GlStateManager.color4f(1, 1, 1, 1);

            int contentWidth = (this.iconLocation != null ? this.iconWidth : 0) + this.textWidth;
            if (this.iconLocation != null && !StringUtils.isNullOrEmpty(this.rawText))
                contentWidth += 3;
            int contentX = (int) Math.ceil((this.width - contentWidth) / 2.0);

            if (this.iconLocation != null)
            {
                int iconY = (this.height - this.iconHeight) / 2;
                Minecraft.getInstance().getTextureManager().bindTexture(this.iconLocation);
                ShapeRenderer.drawRectWithTexture(posX + this.x + contentX, posY + this.y + iconY, this.iconU, this.iconV, this.iconWidth, this.iconHeight, this.iconWidth, this.iconHeight, this.iconSourceWidth, this.iconSourceHeight);
            }

            if (!StringUtils.isNullOrEmpty(this.rawText))
            {
                int textY = (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
                int textOffsetX = this.iconLocation != null ? this.iconWidth + 3 : 0;
                int textColor = this.state == ButtonState.DISABLED ? this.disabledTextColor : hovered ? this.hoveredTextColor : this.buttonColor;
                this.fontRenderer.drawString(this.rawText, posX + this.x + contentX + textOffsetX, posY + this.y + textY, textColor);
            }
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            if (this.lastTooltip == Long.MAX_VALUE)
                this.lastTooltip = System.currentTimeMillis();
            if (System.currentTimeMillis() - this.lastTooltip >= this.tooltipDelay)
                renderer.renderComponentHoverEffect(this.text, mouseX, mouseY);
        }
        else
        {
            this.lastTooltip = Long.MAX_VALUE;
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            if (this.clickListener != null)
            {
                if (this.clickListener.handle(mouseX, mouseY, mouseButton))
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
        return super.onMousePressed(mouseX, mouseY, mouseButton);
    }

    /**
     * Sets the icon to null.
     */
    public ButtonComponent removeIcon()
    {
        this.iconLocation = null;
        this.getValueSerializer().markDirty("icon");
        this.updateSize();
        return this;
    }

    @Override
    public float getX()
    {
        return this.x;
    }

    @Override
    public float getY()
    {
        return this.y;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The pixels between the content and the edge of the button
     */
    public int getPadding()
    {
        return this.padding;
    }

    /**
     * @return Whether or not the width is being automatically set
     */
    public boolean isExplicitWidth()
    {
        return explicitWidth;
    }

    /**
     * @return Whether or not the height is being automatically set
     */
    public boolean isExplicitHeight()
    {
        return explicitHeight;
    }

    /**
     * @return The font renderer used to draw text
     */
    public FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    /**
     * @return The text displayed on the button or null if there is no text displayed
     */
    @Nullable
    public ITextComponent getText()
    {
        return text;
    }

    /**
     * @return The time it takes for tooltips to begin rendering in ms
     */
    public long getTooltipDelay()
    {
        return tooltipDelay;
    }

    /**
     * @return The location of the icon texture
     */
    public ResourceLocation getIconLocation()
    {
        return iconLocation;
    }

    /**
     * @return The x position on the texture to start rendering
     */
    public int getIconU()
    {
        return iconU;
    }

    /**
     * @return The y position on the texture to start rendering
     */
    public int getIconV()
    {
        return iconV;
    }

    /**
     * @return The x size on the texture to fetch
     */
    public int getIconWidth()
    {
        return iconWidth;
    }

    /**
     * @return The y size of the texture to fetch
     */
    public int getIconHeight()
    {
        return iconHeight;
    }

    /**
     * @return The width of the icon source texture
     */
    public int getIconSourceWidth()
    {
        return iconSourceWidth;
    }

    /**
     * @return The height of the icon source texture
     */
    public int getIconSourceHeight()
    {
        return iconSourceHeight;
    }

    /**
     * @return The state this component is currently displayed in
     */
    public ButtonState getState()
    {
        return state;
    }

    /**
     * @return The color of the buttons when they are normal
     */
    public int getButtonColor()
    {
        return this.buttonColor;
    }

    /**
     * @return The color of the buttons when they are hovered
     */
    public int getHoveredButtonColor()
    {
        return this.hoveredButtonColor;
    }

    /**
     * @return The color of the text when its disabled
     */
    public int getDisabledTextColor()
    {
        return disabledTextColor;
    }

    /**
     * @return The color of text when its normal
     */
    public int getTextColor()
    {
        return this.textColor;
    }

    /**
     * @return The color of text when its hovered
     */
    public int getHoveredTextColor()
    {
        return this.hoveredTextColor;
    }

    /**
     * @return The set click listener or null if there is no click listener
     */
    @Nullable
    public ClickListener getClickListener()
    {
        return clickListener;
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public ButtonComponent setX(float x)
    {
        this.x = x;
        this.getValueSerializer().markDirty("x");
        return this;
    }

    /**
     * Sets the y position of this component to the specified value.
     *
     * @param y The new y position
     */
    public ButtonComponent setY(float y)
    {
        this.y = y;
        this.getValueSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the position of this component to the specified values.
     *
     * @param x The new x position
     * @param y The new y position
     */
    public ButtonComponent setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.getValueSerializer().markDirty("x");
        this.getValueSerializer().markDirty("y");
        return this;
    }

    /**
     * Sets the x size of this component to the specified values. Disables auto-resizing of the width.
     *
     * @param width The new x size
     */
    public ButtonComponent setWidth(int width)
    {
        this.width = width;
        this.explicitWidth = true;
        this.getValueSerializer().markDirty("width");
        return this;
    }

    /**
     * Sets the y size of this component to the specified values. Disables auto-resizing of the height.
     *
     * @param height The new y size
     */
    public ButtonComponent setHeight(int height)
    {
        this.height = height;
        this.explicitHeight = true;
        this.getValueSerializer().markDirty("height");
        return this;
    }

    /**
     * Sets the size of this component to the specified values. Disables auto-resizing of the width and height.
     *
     * @param width  The new x size
     * @param height The new y size
     */
    public ButtonComponent setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.explicitWidth = true;
        this.explicitHeight = true;
        this.getValueSerializer().markDirty("width");
        this.getValueSerializer().markDirty("height");
        this.markDirty();
        return this;
    }

    /**
     * Sets the pixels between the content and the edge of the button.
     *
     * @param padding The new padding of the button
     */
    public ButtonComponent setPadding(int padding)
    {
        this.padding = padding;
        this.updateSize();
        return this;
    }

    /**
     * Sets the font renderer used to draw text.
     *
     * @param fontRenderer The new font renderer
     */
    public ButtonComponent setFontRenderer(ResourceLocation fontRenderer)
    {
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
        this.fontRendererLocation = fontRenderer;
        this.getValueSerializer().markDirty("fontRenderer");
        this.updateTextCache();
        this.updateSize();
        return this;
    }

    /**
     * Sets the text displayed on the button to the specified information.
     *
     * @param text The new text to display
     */
    public ButtonComponent setText(ITextComponent text)
    {
        this.text = text;
        this.getValueSerializer().markDirty("text");
        this.updateTextCache();
        this.updateSize();
        return this;
    }

    /**
     * Sets the amount of time in the specified time unit it takes for a tooltip to begin rendering.
     *
     * @param unit         The time unit to use
     * @param tooltipDelay The time it takes for tooltips to begin rendering
     */
    public ButtonComponent setTooltipDelay(TimeUnit unit, long tooltipDelay)
    {
        this.tooltipDelay = Math.max(0, unit.toMillis(tooltipDelay));
        this.getValueSerializer().markDirty("tooltipDelay");
        return this;
    }

    /**
     * Sets the icon to the provided {@link IIcon}.
     *
     * @param icon The icon to fetch the image data from
     */
    public ButtonComponent setIcon(IIcon icon)
    {
        this.setIcon(icon.getIconLocation(), icon.getU(), icon.getV(), icon.getWidth(), icon.getHeight(), icon.getSourceWidth(), icon.getSourceHeight());
        return this;
    }

    /**
     * Sets the icon to the specified location, u, v, width, height and a source texture size of 256x256.
     *
     * @param location The location of the icon texture
     * @param u        The x position on the texture to start rendering
     * @param v        The y position on the texture to start rendering
     * @param width    The x size on the texture to fetch
     * @param height   The y size of the texture to fetch
     */
    public ButtonComponent setIcon(ResourceLocation location, int u, int v, int width, int height)
    {
        this.setIcon(location, u, v, width, height, 256, 256);
        return this;
    }

    /**
     * Sets the icon to the specified location, u, v, width, height, source width, and source height.
     *
     * @param location     The location of the icon texture
     * @param u            The x position on the texture to start rendering
     * @param v            The y position on the texture to start rendering
     * @param width        The x size on the texture to fetch
     * @param height       The y size of the texture to fetch
     * @param sourceWidth  The width of the icon source texture
     * @param sourceHeight The height of the icon source texture
     */
    public ButtonComponent setIcon(ResourceLocation location, int u, int v, int width, int height, int sourceWidth, int sourceHeight)
    {
        this.iconLocation = location;
        this.iconU = u;
        this.iconV = v;
        this.iconWidth = width;
        this.iconHeight = height;
        this.iconSourceWidth = sourceWidth;
        this.iconSourceHeight = sourceHeight;
        this.getValueSerializer().markDirty("icon");
        this.updateSize();
        return this;
    }

    /**
     * Sets the rendering state of this component.
     *
     * @param state The new state to use
     */
    public ButtonComponent setState(ButtonState state)
    {
        this.state = state;
        this.getValueSerializer().markDirty("state");
        return this;
    }

    /**
     * Sets the color of the buttons when they are normal, disabled, and hovered.
     *
     * @param color         The normal color or 0 for system default
     * @param disabledColor The color to use when disabled or 0 for system default
     * @param hoveredColor  The color to use when hovered or 0 for system default
     */
    public ButtonComponent setButtonColor(int color, int disabledColor, int hoveredColor)
    {
        this.buttonColor = color;
        this.disabledButtonColor = disabledColor;
        this.hoveredButtonColor = hoveredColor;
        this.getValueSerializer().markDirty("buttonColor");
        this.getValueSerializer().markDirty("disabledButtonColor");
        this.getValueSerializer().markDirty("hoveredButtonColor");
        return this;
    }

    /**
     * Sets the color of the text on the buttons when they are normal, disabled, and hovered.
     *
     * @param color         The normal color or 0 for system default
     * @param disabledColor The color to use when disabled or 0 for system default
     * @param hoveredColor  The color to use when hovered or 0 for system default
     */
    public ButtonComponent setTextColor(int color, int disabledColor, int hoveredColor)
    {
        this.textColor = color;
        this.disabledTextColor = disabledColor;
        this.hoveredTextColor = hoveredColor;
        this.getValueSerializer().markDirty("textColor");
        this.getValueSerializer().markDirty("disabledTextColor");
        this.getValueSerializer().markDirty("hoveredTextColor");
        return this;
    }

    /**
     * Sets the click listener for this text.
     *
     * @param clickListener The new click listener to use or null to remove the listener
     */
    public ButtonComponent setClickListener(@Nullable ClickListener clickListener)
    {
        this.clickListener = clickListener;
        return this;
    }
}
