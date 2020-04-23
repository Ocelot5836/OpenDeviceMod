package com.ocelot.opendevices.container;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderLayoutManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;

public class ComponentBuilderLayoutTab extends AbstractButton
{
    private final ComponentBuilderScreen parent;
    private final ComponentBuilderLayoutManager layoutManager;
    private final int index;

    public ComponentBuilderLayoutTab(ComponentBuilderScreen parent, ComponentBuilderLayoutManager layoutManager, int x, int y, int index)
    {
        super(x, y, 24, 24, "");
        this.parent = parent;
        this.layoutManager = layoutManager;
        this.index = index;
    }

    private boolean isSelected()
    {
        return this.parent.getSelectedIndex() + this.parent.getScroll() == this.index;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(ComponentBuilderScreen.CONTAINER_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(this.x, this.y, 176, this.isSelected() ? this.height : 0, this.width, this.height);
    }

    @Override
    public void onPress()
    {
        this.parent.setSelectedIndex(this.index - this.parent.getScroll());
    }
}
