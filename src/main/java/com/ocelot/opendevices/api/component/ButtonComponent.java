package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Allows the addition of buttons to a {@link Layout}.</p>
 *
 * @author Ocelot
 * @see Layout
 */
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
    private List<ITextComponent> tooltip;
    private long tooltipDelay;
    private long lastTooltip;

    private ResourceLocation iconLocation;
    private int iconU;
    private int iconV;
    private int iconWidth;
    private int iconHeight;
    private int iconTextureWidth;
    private int iconTextureHeight;

    private ButtonState state;

    public ButtonComponent()
    {
        this.fontRendererLocation = Minecraft.DEFAULT_FONT_RENDERER_NAME;
        this.padding = 5;
        this.lastTooltip = Long.MAX_VALUE;
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
        this.tooltip = new ArrayList<>(other.tooltip);
        this.tooltipDelay = other.tooltipDelay;

        this.iconLocation = other.iconLocation;
        this.iconU = other.iconU;
        this.iconV = other.iconV;
        this.iconWidth = other.iconWidth;
        this.iconHeight = other.iconHeight;
        this.iconTextureWidth = other.iconTextureWidth;
        this.iconTextureHeight = other.iconTextureHeight;

        this.state = other.state;
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
            width += this.fontRenderer.getStringWidth(this.text.getFormattedText());
            height = 16;
        }

        if (this.iconLocation != null && this.text != null)
        {
            width += 3;
            height = this.iconHeight + this.padding * 2;
        }

        if (!this.explicitWidth)
            this.width = width;
        if (!this.explicitHeight)
            this.height = height;
    }

    @Override
    public void update()
    {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
//        if (this.state != ButtonState.INVISIBLE)
//        {
//            Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.COMPONENTS_LOCATION);
//            RenderUtil.glColor(this.getWindow().getLaptop().readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
//            Color bgColor = new Color(getColorScheme().getBackgroundColor()).brighter().brighter();
//            float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
//            bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1.0F));
//            GL11.glColor4f(bgColor.getRed() / 255F, bgColor.getGreen() / 255F, bgColor.getBlue() / 255F, 1.0F);
//            this.hovered = GuiHelper.isMouseWithin(mouseX, mouseY, x, y, width, height) && windowActive;
//            int i = this.getHoverState(this.hovered);
//            GlStateManager.enableBlend();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//            GlStateManager.blendFunc(770, 771);
//
//            /* Corners */
//            RenderUtil.drawRectWithTexture(x, y, 96 + i * 5, 12, 2, 2, 2, 2);
//            RenderUtil.drawRectWithTexture(x + width - 2, y, 99 + i * 5, 12, 2, 2, 2, 2);
//            RenderUtil.drawRectWithTexture(x + width - 2, y + height - 2, 99 + i * 5, 15, 2, 2, 2, 2);
//            RenderUtil.drawRectWithTexture(x, y + height - 2, 96 + i * 5, 15, 2, 2, 2, 2);
//
//            /* Middles */
//            RenderUtil.drawRectWithTexture(x + 2, y, 98 + i * 5, 12, width - 4, 2, 1, 2);
//            RenderUtil.drawRectWithTexture(x + width - 2, y + 2, 99 + i * 5, 14, 2, height - 4, 2, 1);
//            RenderUtil.drawRectWithTexture(x + 2, y + height - 2, 98 + i * 5, 15, width - 4, 2, 1, 2);
//            RenderUtil.drawRectWithTexture(x, y + 2, 96 + i * 5, 14, 2, height - 4, 2, 1);
//
//            /* Center */
//            RenderUtil.drawRectWithTexture(x + 2, y + 2, 98 + i * 5, 14, width - 4, height - 4, 1, 1);
//
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//            int contentWidth = (iconResource != null ? iconWidth: 0) + getTextWidth(text);
//            if(iconResource != null && !StringUtils.isNullOrEmpty(text)) contentWidth += 3;
//            int contentX = (int) Math.ceil((width - contentWidth) / 2.0);
//
//            if(iconResource != null)
//            {
//                int iconY = (height - iconHeight) / 2;
//                mc.getTextureManager().bindTexture(iconResource);
//                RenderUtil.drawRectWithTexture(x + contentX, y + iconY, iconU, iconV, iconWidth, iconHeight, iconWidth, iconHeight, iconSourceWidth, iconSourceHeight);
//            }
//
//            if(!StringUtils.isNullOrEmpty(text))
//            {
//                int textY = (height - mc.fontRenderer.FONT_HEIGHT) / 2 + 1;
//                int textOffsetX = iconResource != null ? iconWidth + 3 : 0;
//                int textColor = !Button.this.enabled ? 10526880 : (Button.this.hovered ? 16777120 : 14737632);
//                drawString(mc.fontRenderer, text, x + contentX + textOffsetX, y + textY, textColor);
//            }
//        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
if(this.isHovered(mouseX, mouseY)){

}
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
     * The font renderer used to draw text
     */
    public FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    /**
     * @return The new text to display
     */
    @Nullable
    public ITextComponent getText()
    {
        return text;
    }

    /**
     * @return The lines to use as the tooltip
     */
    @Nullable
    public List<ITextComponent> getTooltip()
    {
        return tooltip;
    }

    /**
     * @return The time it takes for tooltips to begin rendering
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
        this.updateSize();
        return this;
    }

    /**
     * Sets the tooltip lines to the provided tooltip values or null to remove the tooltip.
     *
     * @param lines The lines to use as the tooltip
     */
    public ButtonComponent setTooltip(@Nullable ITextComponent... lines)
    {
        this.setTooltip(lines == null || lines.length == 0 ? null : Arrays.asList(lines));
        return this;
    }

    /**
     * Sets the tooltip lines to the provided tooltip values or null to remove the tooltip.
     *
     * @param lines The lines to use as the tooltip
     */
    public ButtonComponent setTooltip(@Nullable List<ITextComponent> lines)
    {
        if (lines == null || lines.isEmpty())
        {
            this.tooltip = null;
        }
        else
        {
            if (this.tooltip == null)
                this.tooltip = new ArrayList<>();
            this.tooltip.addAll(lines);
        }
        return this;
    }

    /**
     * Sets the amount of time in ms it takes for a tooltip to begin rendering.
     *
     * @param tooltipDelay The time it takes for tooltips to begin rendering
     */
    public ButtonComponent setTooltipDelay(long tooltipDelay)
    {
        this.tooltipDelay = tooltipDelay;
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

        if (this.tooltip != null)
        {
            ListNBT tooltipList = new ListNBT();
            this.tooltip.forEach(text -> tooltipList.add(new StringNBT(ITextComponent.Serializer.toJson(text))));
            nbt.put("tooltip", tooltipList);

            nbt.putLong("tooltipDelay", this.tooltipDelay);
        }

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

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");

        this.setFontRenderer(new ResourceLocation(nbt.getString("fontRenderer")));

        if (nbt.contains("text", Constants.NBT.TAG_STRING))
        {
            this.text = ITextComponent.Serializer.fromJson(nbt.getString("text"));
        }

        if (nbt.contains("tooltip", Constants.NBT.TAG_LIST))
        {
            this.tooltip = new ArrayList<>();

            ListNBT tooltipList = nbt.getList("tooltip", Constants.NBT.TAG_STRING);
            for (int i = 0; i < tooltipList.size(); i++)
            {
                this.tooltip.add(ITextComponent.Serializer.fromJson(tooltipList.getString(i)));
            }

            this.tooltipDelay = nbt.getLong("tooltipDelay");
        }

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
    }
}
