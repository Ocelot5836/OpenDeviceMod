package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.handler.ClickListener;
import com.ocelot.opendevices.api.util.IIcon;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL11.*;

/**
 * <p>Allows the addition of clickable buttons to a {@link Layout}.</p>
 *
 * @author Ocelot
 * @see Layout
 */
@SuppressWarnings("unused")
@Component.Register(OpenDevices.MOD_ID + ":button")
public class ButtonComponent extends BasicComponent
{
    private int x;
    private int y;
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
    private int iconTextureWidth;
    private int iconTextureHeight;

    private ButtonState state;
    //TODO implement button colors
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

    public ButtonComponent()
    {
        this(0, 0);
    }

    public ButtonComponent(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.setPadding(5);
        this.setFontRenderer(Minecraft.DEFAULT_FONT_RENDERER_NAME);
        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.lastTooltip = Long.MAX_VALUE;

        this.state = ButtonState.VISIBLE;
    }

    public ButtonComponent(ButtonComponent other)
    {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
        this.padding = other.padding;
        this.explicitWidth = other.explicitWidth;
        this.explicitHeight = other.explicitHeight;

        this.fontRenderer = other.fontRenderer;
        this.fontRendererLocation = other.fontRendererLocation;
        this.text = other.text;
        this.tooltipDelay = other.tooltipDelay;

        this.iconLocation = other.iconLocation;
        this.iconU = other.iconU;
        this.iconV = other.iconV;
        this.iconWidth = other.iconWidth;
        this.iconHeight = other.iconHeight;
        this.iconTextureWidth = other.iconTextureWidth;
        this.iconTextureHeight = other.iconTextureHeight;

        this.state = other.state;
        this.disabledButtonColor = other.disabledButtonColor;
        this.buttonColor = other.buttonColor;
        this.hoveredButtonColor = other.hoveredButtonColor;
        this.disabledTextColor = other.disabledTextColor;
        this.textColor = other.textColor;
        this.hoveredTextColor = other.hoveredTextColor;

        this.updateTextCache();
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
            this.width = width;
        if (!this.explicitHeight)
            this.height = height;
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
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.state != ButtonState.INVISIBLE)
        {
            boolean hovered = this.isHovered(mouseX, mouseY) && this.getWindow().isTop();
            int offset = this.state == ButtonState.DISABLED ? 0 : hovered ? 2 : 1;
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.COMPONENTS_LOCATION);
            //                    RenderUtil.glColor(this.getWindow().getLaptop().readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));

            /* Corners */
            RenderUtil.drawRectWithTexture(x, y, 96 + offset * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(x + width - 2, y, 99 + offset * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(x + width - 2, y + height - 2, 99 + offset * 5, 15, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(x, y + height - 2, 96 + offset * 5, 15, 2, 2, 2, 2);

            /* Middles */
            RenderUtil.drawRectWithTexture(x + 2, y, 98 + offset * 5, 12, width - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(x + width - 2, y + 2, 99 + offset * 5, 14, 2, height - 4, 2, 1);
            RenderUtil.drawRectWithTexture(x + 2, y + height - 2, 98 + offset * 5, 15, width - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(x, y + 2, 96 + offset * 5, 14, 2, height - 4, 2, 1);

            /* Center */
            RenderUtil.drawRectWithTexture(x + 2, y + 2, 98 + offset * 5, 14, width - 4, height - 4, 1, 1);

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            int contentWidth = (this.iconLocation != null ? this.iconWidth : 0) + this.textWidth;
            if (this.iconLocation != null && !StringUtils.isNullOrEmpty(this.rawText))
                contentWidth += 3;
            int contentX = (int) Math.ceil((this.width - contentWidth) / 2.0);

            if (this.iconLocation != null)
            {
                int iconY = (this.height - this.iconHeight) / 2;
                Minecraft.getInstance().getTextureManager().bindTexture(this.iconLocation);
                RenderUtil.drawRectWithTexture(this.x + contentX, this.y + iconY, this.iconU, this.iconV, this.iconWidth, this.iconHeight, this.iconWidth, this.iconHeight, this.iconTextureWidth, this.iconTextureHeight);
            }

            if (!StringUtils.isNullOrEmpty(this.rawText))
            {
                int textY = (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
                int textOffsetX = this.iconLocation != null ? this.iconWidth + 3 : 0;
                int textColor = this.state == ButtonState.DISABLED ? 10526880 : (hovered ? 16777120 : 14737632);
                this.fontRenderer.drawString(this.rawText, this.x + contentX + textOffsetX, this.y + textY, textColor);
            }
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getWindow().isTop() && this.isHovered(mouseX, mouseY))
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
        if (this.clickListener != null && this.isHovered(mouseX, mouseY)&&this.clickListener.handle(mouseX, mouseY, mouseButton))
        {
            this.playPressSound(mouseX, mouseY, mouseButton);
            return true;
        }
        return super.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public ButtonComponent copy()
    {
        return new ButtonComponent(this);
    }

    /**
     * Sets the icon to null.
     */
    public ButtonComponent removeIcon()
    {
        this.iconLocation = null;
        this.updateSize();
        return this;
    }

    @Override
    public int getX()
    {
        return this.x;
    }

    @Override
    public int getY()
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
     * @return The width of the entire texture
     */
    public int getIconTextureWidth()
    {
        return iconTextureWidth;
    }

    /**
     * @return The height of the entire texture
     */
    public int getIconTextureHeight()
    {
        return iconTextureHeight;
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
     * Sets the x size of this component to the specified values. Disables auto-resizing of the width.
     *
     * @param width The new x size
     */
    public ButtonComponent setWidth(int width)
    {
        this.width = width;
        this.explicitWidth = true;
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
        this.updateTextCache();
        this.updateSize();
        return this;
    }

    /**
     * Sets the amount of time in ms <i>(1/1000 of a second)</i> it takes for a tooltip to begin rendering.
     *
     * @param tooltipDelay The time it takes for tooltips to begin rendering in ms
     */
    public ButtonComponent setTooltipDelay(long tooltipDelay)
    {
        this.tooltipDelay = Math.max(0, tooltipDelay);
        return this;
    }

    /**
     * Sets the icon to the provided {@link IIcon}.
     *
     * @param icon The icon to fetch the icon data from
     */
    public ButtonComponent setIcon(IIcon icon)
    {
        this.setIcon(icon.getIconLocation(), icon.getU(), icon.getV(), icon.getIconSize(), icon.getIconSize(), icon.getTextureWidth(), icon.getTextureHeight());
        return this;
    }

    /**
     * Sets the icon to the specified location, u, v, width, height and a texture size of 256x256.
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
     * Sets the icon to the specified location, u, v, width, height, texture width, and texture height.
     *
     * @param location      The location of the icon texture
     * @param u             The x position on the texture to start rendering
     * @param v             The y position on the texture to start rendering
     * @param width         The x size on the texture to fetch
     * @param height        The y size of the texture to fetch
     * @param textureWidth  The width of the entire texture
     * @param textureHeight The height of the entire texture
     */
    public ButtonComponent setIcon(ResourceLocation location, int u, int v, int width, int height, int textureWidth, int textureHeight)
    {
        this.iconLocation = location;
        this.iconU = u;
        this.iconV = v;
        this.iconWidth = width;
        this.iconHeight = height;
        this.iconTextureWidth = textureWidth;
        this.iconTextureHeight = textureHeight;
        this.updateSize();
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

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);

        nbt.putString("fontRenderer", this.fontRendererLocation.toString());
        if (this.text != null)
            nbt.putString("text", ITextComponent.Serializer.toJson(this.text));

        nbt.putLong("tooltipDelay", this.tooltipDelay);

        if (this.iconLocation != null)
        {
            nbt.putString("iconLocation", this.iconLocation.toString());
            nbt.putInt("iconU", this.iconU);
            nbt.putInt("iconV", this.iconV);
            nbt.putInt("iconWidth", this.iconWidth);
            nbt.putInt("iconHeight", this.iconHeight);
            nbt.putInt("iconTextureWidth", this.iconTextureWidth);
            nbt.putInt("iconTextureHeight", this.iconTextureHeight);
        }

        nbt.putByte("state", (byte) this.state.ordinal());
        nbt.putInt("disabledButtonColor", this.disabledButtonColor);
        nbt.putInt("buttonColor", this.buttonColor);
        nbt.putInt("hoveredButtonColor", this.hoveredButtonColor);
        nbt.putInt("disabledTextColor", this.disabledTextColor);
        nbt.putInt("textColor", this.textColor);
        nbt.putInt("hoveredTextColor", this.hoveredTextColor);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");

        this.fontRendererLocation = new ResourceLocation(nbt.getString("fontRenderer"));
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(this.fontRendererLocation);

        if (nbt.contains("text", Constants.NBT.TAG_STRING))
        {
            this.text = ITextComponent.Serializer.fromJson(nbt.getString("text"));
        }

        this.updateTextCache();

        this.tooltipDelay = Math.max(0, nbt.getLong("tooltipDelay"));

        if (nbt.contains("iconLocation", Constants.NBT.TAG_STRING))
        {
            this.iconLocation = new ResourceLocation(nbt.getString("iconLocation"));
            this.iconU = nbt.getInt("iconU");
            this.iconV = nbt.getInt("iconV");
            this.iconWidth = nbt.getInt("iconWidth");
            this.iconHeight = nbt.getInt("iconHeight");
            this.iconTextureWidth = nbt.getInt("iconTextureWidth");
            this.iconTextureHeight = nbt.getInt("iconTextureHeight");
        }

        this.state = ButtonState.values()[nbt.getByte("state") & ButtonState.values().length];
        this.disabledButtonColor = nbt.getInt("disabledButtonColor");
        this.buttonColor = nbt.getInt("buttonColor");
        this.hoveredButtonColor = nbt.getInt("hoveredButtonColor");
        this.disabledTextColor = nbt.getInt("disabledTextColor");
        this.textColor = nbt.getInt("textColor");
        this.hoveredTextColor = nbt.getInt("hoveredTextColor");
    }
}
