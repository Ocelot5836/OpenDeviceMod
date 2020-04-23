package com.ocelot.opendevices.container;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Yoinked from Filters :p.
 *
 * @author MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class IconButton extends Button
{
    private ResourceLocation iconResource;
    private int iconU;
    private int iconV;

    public IconButton(int x, int y, String message, IPressable pressable, ResourceLocation iconResource, int iconU, int iconV)
    {
        super(x, y, 20, 20, message, pressable);
        this.iconResource = iconResource;
        this.iconU = iconU;
        this.iconV = iconV;
    }

    public void setIcon(ResourceLocation iconResource, int iconU, int iconV)
    {
        this.iconResource = iconResource;
        this.iconU = iconU;
        this.iconV = iconV;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int offset = this.getYImage(this.isHovered());
        this.blit(this.x, this.y, 0, 46 + offset * 20, this.width / 2, this.height);
        this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + offset * 20, this.width / 2, this.height);
        if (!this.active)
        {
            RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
        }
        Minecraft.getInstance().getTextureManager().bindTexture(this.iconResource);
        this.blit(this.x + 2, this.y + 2, this.iconU, this.iconV, 16, 16);
    }
}
