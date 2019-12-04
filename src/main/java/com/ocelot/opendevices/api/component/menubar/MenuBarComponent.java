package com.ocelot.opendevices.api.component.menubar;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.LaptopSettings;
import com.ocelot.opendevices.api.component.BasicComponent;
import com.ocelot.opendevices.api.component.ButtonState;
import com.ocelot.opendevices.api.component.Component;
import com.ocelot.opendevices.api.handler.ComponentClickListener;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * <p>A component that displays a bar with selectable options and buttons. Uses {@link MenuBarItem} </p>
 *
 * @author Ocelot
 * @see MenuBarItem
 */
@Component.Register(OpenDevices.MOD_ID + ":menu_bar")
public class MenuBarComponent extends BasicComponent
{
    private int x;
    private int y;
    private int width;
    private int height;
    private int itemPadding;

    private FontRenderer fontRenderer;
    private ResourceLocation fontRendererLocation;
    private List<MenuBarItem> items;
    private boolean border;
    private int color;

    private ButtonState state;
    private int disabledTextColor;
    private int textColor;
    private int hoveredTextColor;

    private int hoveredIndex;
    private int selectedIndex;
    private boolean hasScroll;
    private double scroll;
    private int maxScroll;
    private int lastTooltipIndex;
    private long lastTooltip;
    private ComponentClickListener<MenuBarItem> clickListener;

    public MenuBarComponent(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemPadding = 2;

        this.items = new ArrayList<>();
        this.setFontRenderer(Minecraft.DEFAULT_FONT_RENDERER_NAME);

        this.state = ButtonState.VISIBLE;
        this.hoveredIndex = -1;
        this.selectedIndex = -1;
        this.lastTooltip = Long.MAX_VALUE;
    }

    private void updateSize()
    {
        int totalWidth = 0;
        for (MenuBarItem item : this.items)
        {
            totalWidth += this.getWidth(item) + this.itemPadding * 2;
        }

        this.hasScroll = totalWidth >= this.width;
        if (this.hasScroll)
        {
            this.scroll = 0;
            this.maxScroll = totalWidth - this.width;
        }
    }

    private void updateSelectedIndex(double mouseX, double mouseY)
    {
        if (this.isHovered(mouseX, mouseY) && this.selectedIndex != -1)
        {
            int selectedIndex = -1;
            for (MenuBarItem item : this.items)
            {
                if (this.isHovered(mouseX, mouseY, item))
                {
                    selectedIndex = this.items.indexOf(item);
                    break;
                }
            }

            if (selectedIndex != -1)
            {
                this.selectedIndex = selectedIndex;
            }
        }
    }

    private int getWidth(MenuBarItem item)
    {
        String text = item.getRawText();
        int width = (item.getIconLocation() != null ? item.getIconWidth() : 0) + (text != null ? this.fontRenderer.getStringWidth(text) : 0);
        if (item.getIconLocation() != null && !StringUtils.isNullOrEmpty(text))
            width += 3;
        return width;
    }

    private boolean isHovered(double mouseX, double mouseY, MenuBarItem item)
    {
        if (!this.isHovered(mouseX, mouseY) || !this.items.contains(item))
            return false;

        int xOffset = 0;
        for (MenuBarItem i : this.items)
        {
            int width = this.getWidth(i) + this.itemPadding * 2;
            if (i == item)
            {
                return RenderUtil.isMouseInside(mouseX, mouseY, this.getWindowX() + this.x + xOffset - (float) this.scroll, this.getWindowY() + this.y, this.getWindowX() + this.x + xOffset + width - (float) this.scroll, this.getWindowY() + this.getMaxY());
            }

            xOffset += width;
        }

        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.state != ButtonState.INVISIBLE)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            Minecraft.getInstance().getTextureManager().bindTexture(DeviceConstants.COMPONENTS_LOCATION);

            RenderUtil.glColor(0xFF36393C);
            if (this.border)
            {
                RenderUtil.drawSizedRectWithTexture(this.x, this.y, 111, 12, this.width, this.height, 1, 1);
            }
            else
            {
                RenderUtil.drawRectWithTexture(this.x, this.y, 112, 13, this.width, this.height, 1, 1);
            }
            GlStateManager.color4f(1f, 1f, 1f, 1f);

            RenderUtil.pushScissor(this.getWindowX() + this.x, this.getWindowY() + this.y, this.width, this.height);

            this.hoveredIndex = -1;
            int xOffset = this.itemPadding;
            for (MenuBarItem item : this.items)
            {
                int width = this.getWidth(item) + this.itemPadding;

                if (xOffset + width - (float) this.scroll < 0)
                {
                    xOffset += width + this.itemPadding;
                    continue;
                }
                if (xOffset - (float) this.scroll >= this.width)
                    break;

                String text = item.getRawText();
                boolean hovered = this.getWindow().isTop() && this.isHovered(mouseX, mouseY, item);

                if (item.getIconLocation() != null)
                {
                    int iconY = (this.height - item.getIconHeight()) / 2;
                    Minecraft.getInstance().getTextureManager().bindTexture(item.getIconLocation());
                    RenderUtil.drawRectWithTexture(this.x + xOffset - (float) this.scroll, this.y + iconY, item.getIconU(), item.getIconV(), item.getIconWidth(), item.getIconHeight(), item.getIconWidth(), item.getIconHeight(), item.getIconTextureWidth(), item.getIconTextureHeight());
                }

                if (!StringUtils.isNullOrEmpty(text))
                {
                    int textY = (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
                    int textOffsetX = item.getIconLocation() != null ? item.getIconWidth() + 3 : 0;
                    int textColor = this.state == ButtonState.DISABLED ? this.getDisabledTextColor() : (this.getSelectedItem() == item || hovered ? this.getHoveredTextColor() : this.getTextColor());
                    this.fontRenderer.drawString(text, this.x + xOffset + textOffsetX - (float) this.scroll, this.y + textY, textColor);
                }

                if (hovered)
                {
                    this.hoveredIndex = this.items.indexOf(item);
                }

                xOffset += width + this.itemPadding;
            }

            RenderUtil.popScissor();

            if (this.selectedIndex != -1)
            {
                MenuBarItem item = this.items.get(this.selectedIndex);
                //TODO render the drop-down menu
                fill(this.x, this.getMaxY(), this.x + this.getWidth(item), this.getMaxY() + 64, 0xffff00ff);
                this.fontRenderer.drawString("WIP", this.x + 1, this.getMaxY() + 1, this.getWindow().getLaptop().readSetting(LaptopSettings.DESKTOP_TEXT_COLOR));
            }
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        if (this.state != ButtonState.INVISIBLE && this.getWindow().isTop() && this.isHovered(mouseX, mouseY) && this.hoveredIndex != -1)
        {
            if (this.lastTooltipIndex != this.hoveredIndex)
            {
                this.lastTooltipIndex = this.hoveredIndex;
                this.lastTooltip = Long.MAX_VALUE;
            }
            MenuBarItem item = this.items.get(this.hoveredIndex);
            if (this.lastTooltip == Long.MAX_VALUE)
                this.lastTooltip = System.currentTimeMillis();
            if (System.currentTimeMillis() - this.lastTooltip >= item.getTooltipDelay())
                renderer.renderComponentHoverEffect(item.getText(), mouseX, mouseY);
        }
        else
        {
            this.lastTooltipIndex = -1;
            this.lastTooltip = Long.MAX_VALUE;
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        if (this.state != ButtonState.INVISIBLE)
        {
            int selectedIndex = -1;
            if (this.isHovered(mouseX, mouseY))
            {
                for (MenuBarItem item : this.items)
                {
                    if (this.isHovered(mouseX, mouseY, item))
                    {
                        selectedIndex = this.items.indexOf(item);
                        break;
                    }
                }
            }
            this.selectedIndex = this.selectedIndex == selectedIndex ? -1 : selectedIndex;
        }
        else
        {
            this.selectedIndex = -1;
        }

        return super.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        this.updateSelectedIndex(mouseX, mouseY);
        super.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.isHovered(mouseX, mouseY) && this.hasScroll)
        {
            this.scroll += -amount * this.getWindow().getLaptop().readSetting(LaptopSettings.SCROLL_SPEED);
            if (this.scroll < 0)
                this.scroll = 0;
            if (this.scroll > this.maxScroll)
                this.scroll = this.maxScroll;

            this.updateSelectedIndex(mouseX, mouseY);

            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, amount);
    }

    /**
     * Adds the specified items to this component
     *
     * @param items The items to add
     */
    public void add(MenuBarItem... items)
    {
        this.items.addAll(Arrays.asList(items));
        this.updateSize();
    }

    /**
     * Adds the specified items to this component
     *
     * @param items The items to add
     */
    public void add(List<MenuBarItem> items)
    {
        this.items.addAll(items);
        this.updateSize();
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
        return this.height;
    }

    /**
     * @return The amount of space (in pixels) each menu bar item gets between the edges of this component and the size of that component
     */
    public int getItemPadding()
    {
        return itemPadding;
    }

    /**
     * @return Whether or not this menu bar should render a border or not
     */
    public boolean hasBorder()
    {
        return border;
    }

    /**
     * @return The color of the bar
     */
    public int getColor()
    {
        return this.color == 0 ? this.getWindow().getLaptop().readSetting(LaptopSettings.MENU_BAR_COLOR) : this.color;
    }

    /**
     * @return The state this component is currently displayed in
     */
    public ButtonState getState()
    {
        return state;
    }

    /**
     * @return The color of text when its disabled
     */
    public int getDisabledTextColor()
    {
        return this.disabledTextColor == 0 ? this.getWindow().getLaptop().readSetting(LaptopSettings.BUTTON_TEXT_DISABLED_COLOR) : this.disabledTextColor;
    }

    /**
     * @return The color of text when its normal
     */
    public int getTextColor()
    {
        return this.textColor == 0 ? this.getWindow().getLaptop().readSetting(LaptopSettings.BUTTON_TEXT_COLOR) : this.textColor;
    }

    /**
     * @return The color of text when its hovered
     */
    public int getHoveredTextColor()
    {
        return this.hoveredTextColor == 0 ? this.getWindow().getLaptop().readSetting(LaptopSettings.BUTTON_TEXT_HIGHLIGHT_COLOR) : this.hoveredTextColor;
    }

    /**
     * @return The index of the currently selected item rendering its contents
     */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    /**
     * @return The currently selected item rendering its contents
     */
    @Nullable
    public MenuBarItem getSelectedItem()
    {
        return this.selectedIndex == -1 ? null : this.items.get(this.selectedIndex);
    }

    /**
     * Sets the x position of this component to the specified value.
     *
     * @param x The new x position
     */
    public MenuBarComponent setX(int x)
    {
        this.x = x;
        return this;
    }

    /**
     * Sets the y position of this component to the specified value.
     *
     * @param y The new y position
     */
    public MenuBarComponent setY(int y)
    {
        this.y = y;
        return this;
    }

    /**
     * Sets the position of this component to the specified values.
     *
     * @param x The new x position
     * @param y The new y position
     */
    public MenuBarComponent setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets the x size of this component to the specified values.
     *
     * @param width The new x size
     */
    public MenuBarComponent setWidth(int width)
    {
        this.width = width;
        return this;
    }

    /**
     * Sets the y size of this component to the specified values.
     *
     * @param height The new y size
     */
    public MenuBarComponent setHeight(int height)
    {
        this.height = height;
        return this;
    }

    /**
     * Sets the size of this component to the specified values.
     *
     * @param width  The new x size
     * @param height The new y size
     */
    public MenuBarComponent setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets the pixels between the content and the edge of each item.
     *
     * @param padding The new padding of the button
     */
    public MenuBarComponent setItemPadding(int padding)
    {
        this.itemPadding = padding;
        this.updateSize();
        return this;
    }

    /**
     * Sets the font renderer used to draw text.
     *
     * @param fontRenderer The new font renderer
     */
    public MenuBarComponent setFontRenderer(ResourceLocation fontRenderer)
    {
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(fontRenderer);
        this.fontRendererLocation = fontRenderer;
        this.updateSize();
        return this;
    }

    /**
     * Sets whether or not this component should have a border rendered on it.
     *
     * @param border Whether or not to have a border
     */
    public MenuBarComponent setBorder(boolean border)
    {
        this.border = border;
        return this;
    }

    /**
     * Sets the color of the actual bar.
     *
     * @param color The new color or 0 to use the system default
     */
    public MenuBarComponent setColor(int color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets the rendering state of this component.
     *
     * @param state The new state to use
     */
    public MenuBarComponent setState(ButtonState state)
    {
        this.state = state;
        return this;
    }

    /**
     * Sets the color of the text on the buttons when they are normal, disabled, and hovered.
     *
     * @param color         The normal color or 0 for system default
     * @param disabledColor The color to use when disabled or 0 for system default
     * @param hoveredColor  The color to use when hovered or 0 for system default
     */
    public MenuBarComponent setTextColor(int color, int disabledColor, int hoveredColor)
    {
        this.textColor = color;
        this.disabledTextColor = disabledColor;
        this.hoveredTextColor = hoveredColor;
        return this;
    }

    /**
     * Sets the selected item index to the specified index.
     *
     * @param selectedIndex The new index or -1 to deselect
     */
    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex < 0 || selectedIndex >= this.items.size() ? -1 : selectedIndex;
    }

    /**
     * Sets the click listener for this menu bar.
     *
     * @param clickListener The new click listener to use or null to remove the listener
     */
    public MenuBarComponent setClickListener(@Nullable ComponentClickListener<MenuBarItem> clickListener)
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
        nbt.putInt("itemPadding", this.itemPadding);

        nbt.putString("fontRenderer", this.fontRendererLocation.toString());

        ListNBT itemsNBT = new ListNBT();
        this.items.forEach(item -> itemsNBT.add(item.serializeNBT()));
        nbt.put("items", itemsNBT);

        nbt.putBoolean("border", this.border);
        nbt.putInt("color", this.color);

        nbt.putByte("state", this.state.serialize());
        nbt.putInt("disabledTextColor", this.disabledTextColor);
        nbt.putInt("textColor", this.textColor);
        nbt.putInt("hoveredTextColor", this.hoveredTextColor);

        nbt.putInt("selectedIndex", this.selectedIndex);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");
        this.itemPadding = nbt.getInt("itemPadding");

        this.fontRendererLocation = new ResourceLocation(nbt.getString("fontRenderer"));
        this.fontRenderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(this.fontRendererLocation);

        ListNBT itemsNBT = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemsNBT.size(); i++)
        {
            this.add(new MenuBarItem(itemsNBT.getCompound(i)));
        }

        this.border = nbt.getBoolean("border");
        this.color = nbt.getInt("color");

        this.state = ButtonState.deserialize(nbt.getByte("state"));
        this.disabledTextColor = nbt.getInt("disabledTextColor");
        this.textColor = nbt.getInt("textColor");
        this.hoveredTextColor = nbt.getInt("hoveredTextColor");

        this.selectedIndex = nbt.getInt("selectedIndex");
    }
}