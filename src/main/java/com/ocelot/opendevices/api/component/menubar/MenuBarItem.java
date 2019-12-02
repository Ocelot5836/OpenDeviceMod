package com.ocelot.opendevices.api.component.menubar;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.component.ButtonState;
import com.ocelot.opendevices.api.util.IIcon;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * <p>An item that can be added to a {@link MenuBarComponent}. Contains a list of {@link MenuBarItemComponent} that gets rendered as a drop-down box.</p>
 *
 * @author Ocelot
 */
public class MenuBarItem implements INBTSerializable<CompoundNBT>
{
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
    private String rawText;

    public MenuBarItem(CompoundNBT nbt)
    {
        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.state = ButtonState.VISIBLE;

        this.deserializeNBT(nbt);
    }

    public MenuBarItem()
    {
        this.tooltipDelay = DeviceConstants.DEFAULT_TOOLTIP_DELAY;
        this.state = ButtonState.VISIBLE;
    }

    /**
     * Sets the icon to null.
     */
    public MenuBarItem removeIcon()
    {
        this.iconLocation = null;
        return this;
    }

    /**
     * @return The text displayed on the item or null if there is no text displayed
     */
    @Nullable
    public ITextComponent getText()
    {
        return text;
    }

    /**
     * @return The raw string of the text displayed or null if there is no text displayed
     */
    @Nullable
    public String getRawText()
    {
        return rawText;
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
     * @return The state this item is currently displayed in
     */
    public ButtonState getState()
    {
        return state;
    }

    /**
     * Sets the text displayed on the item to the specified information.
     *
     * @param text The new text to display
     */
    public MenuBarItem setText(ITextComponent text)
    {
        this.text = text;
        this.rawText = text.getFormattedText();
        return this;
    }

    /**
     * Sets the amount of time in ms <i>(1/1000 of a second)</i> it takes for a tooltip to begin rendering.
     *
     * @param tooltipDelay The time it takes for tooltips to begin rendering in ms
     */
    public MenuBarItem setTooltipDelay(long tooltipDelay)
    {
        this.tooltipDelay = Math.max(0, tooltipDelay);
        return this;
    }

    /**
     * Sets the icon to the provided {@link IIcon}.
     *
     * @param icon The icon to fetch the icon data from
     */
    public MenuBarItem setIcon(IIcon icon)
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
    public MenuBarItem setIcon(ResourceLocation location, int u, int v, int width, int height)
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
    public MenuBarItem setIcon(ResourceLocation location, int u, int v, int width, int height, int textureWidth, int textureHeight)
    {
        this.iconLocation = location;
        this.iconU = u;
        this.iconV = v;
        this.iconWidth = width;
        this.iconHeight = height;
        this.iconTextureWidth = textureWidth;
        this.iconTextureHeight = textureHeight;
        return this;
    }

    /**
     * Sets the state of this button to the one specified.
     *
     * @param state The new state of this button
     */
    public void setState(ButtonState state)
    {
        this.state = state;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

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

        nbt.putByte("state", this.state.serialize());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt.contains("text", Constants.NBT.TAG_STRING))
        {
            this.text = ITextComponent.Serializer.fromJson(nbt.getString("text"));
            assert this.text != null;
            this.rawText = this.text.getFormattedText();
        }
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

        this.state = ButtonState.deserialize(nbt.getByte("state"));
    }
}
