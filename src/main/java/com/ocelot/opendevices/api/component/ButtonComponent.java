package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.handler.ClickListener;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ButtonComponent extends BasicComponent
{
    private int x;
    private int y;
    private int width;
    private int height;
    private int padding;
    private boolean explicitSize;

    private ITextComponent text;

    private ResourceLocation iconLocation;
    private float iconU;
    private float iconV;
    private float iconWidth;
    private float iconHeight;
    private int iconTextureWidth;
    private int iconTextureHeight;

    private ClickListener clickListener;

    public ButtonComponent()
    {
    }

    public ButtonComponent(Builder builder)
    {
        this.x = builder.x;
        this.y = builder.y;
        this.width = builder.width;
        this.height = builder.height;
        this.padding = builder.padding;
        this.explicitSize = builder.explicitSize;

        this.text = builder.text;

        this.iconLocation = builder.iconLocation;
        this.iconU = builder.iconU;
        this.iconV = builder.iconV;
        this.iconWidth = builder.iconWidth;
        this.iconHeight = builder.iconHeight;
        this.iconTextureWidth = builder.iconTextureWidth;
        this.iconTextureHeight = builder.iconTextureHeight;
    }

    @Override
    public void update()
    {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {

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
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width;
        private int height;
        private int padding;
        private boolean explicitSize;

        private ITextComponent text;

        private ResourceLocation iconLocation;
        private float iconU;
        private float iconV;
        private float iconWidth;
        private float iconHeight;
        private int iconTextureWidth;
        private int iconTextureHeight;

        public Builder()
        {
            this.padding = 5;
        }

        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            this.explicitSize = true;
            return this;
        }

        public Builder setPadding(int padding)
        {
            this.padding = padding;
            return this;
        }

        public Builder setText(ITextComponent text)
        {
            this.text = text;
            return this;
        }

        public Builder setIcon(ResourceLocation location, float u, float v, int width, int height)
        {
            return setIcon(location, u, v, width, height, 256, 256);
        }

        public Builder setIcon(ResourceLocation location, float u, float v, int width, int height, int textureWidth, int textureHeight)
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

        public Builder copy()
        {
            Builder copy = new Builder();
            copy.x = this.x;
            copy.y = this.y;
            copy.width = this.width;
            copy.height = this.height;

            copy.text = this.text;

            copy.padding = this.padding;
            copy.explicitSize = this.explicitSize;

            copy.iconLocation = this.iconLocation;
            copy.iconU = this.iconU;
            copy.iconV = this.iconV;
            copy.iconWidth = this.iconWidth;
            copy.iconHeight = this.iconHeight;
            copy.iconTextureWidth = this.iconTextureWidth;
            copy.iconTextureHeight = this.iconTextureHeight;
            return copy;
        }
    }
}
