package com.ocelot.opendevices.container;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderLayout;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderLayoutManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ComponentBuilderLayoutTab extends AbstractButton
{
    private final ComponentBuilderScreen parent;
    private final ComponentBuilderLayoutManager layoutManager;
    private final ItemRenderer itemRenderer;
    private final FontRenderer fontRenderer;
    private final int index;

    public ComponentBuilderLayoutTab(ComponentBuilderScreen parent, ComponentBuilderLayoutManager layoutManager, int x, int y, int index)
    {
        super(x, y, 24, 24, "");
        this.parent = parent;
        this.layoutManager = layoutManager;
        this.itemRenderer = parent.getMinecraft().getItemRenderer();
        this.fontRenderer = parent.getMinecraft().fontRenderer;
        this.index = index;
    }

    private boolean isSelected()
    {
        return this.parent.getSelectedIndex() == this.index + this.parent.getScroll();
    }

    private ComponentBuilderLayout getLayout()
    {
        return this.layoutManager.getLayout(this.parent.getLayout(this.index + this.parent.getScroll()));
    }

    private void drawIcon(int x, int y)
    {
        this.setBlitOffset(100);
        this.itemRenderer.zLevel = 100.0F;
        RenderSystem.enableRescaleNormal();
        ItemStack stack = this.getLayout().getIcon();
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlays(this.fontRenderer, stack, x, y);
        this.itemRenderer.zLevel = 0.0F;
        this.setBlitOffset(0);
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
        this.drawIcon(this.x + 4, this.y + 4);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY)
    {
        List<String> lines = new ArrayList<>();
        ComponentBuilderLayout layout = this.getLayout();
        lines.add(layout.getTitle().getFormattedText());
        if (this.parent.getMinecraft().gameSettings.advancedItemTooltips)
            lines.add(TextFormatting.DARK_GRAY + String.valueOf(this.parent.getLayout(this.index + this.parent.getScroll())));
        this.parent.renderTooltip(lines, mouseX, mouseY);
    }

    @Override
    public void onPress()
    {
        this.parent.setSelectedIndex(this.index + this.parent.getScroll());
    }
}
