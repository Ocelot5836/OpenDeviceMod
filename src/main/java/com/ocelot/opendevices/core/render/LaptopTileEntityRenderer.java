package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.block.DeviceBlock;
import com.ocelot.opendevices.block.LaptopBlock;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

import static org.lwjgl.opengl.GL30.*;

public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    public static final LaptopTileEntityRenderer INSTANCE = new LaptopTileEntityRenderer();

    private Random random;
    private Framebuffer framebuffer;

    private LaptopTileEntityRenderer()
    {
        this.random = new Random();
        this.framebuffer = null;
    }

    public void delete()
    {
        OpenDevices.LOGGER.debug("Deleting Laptop Render Cache");

        if (this.framebuffer != null)
        {
            this.framebuffer.deleteFramebuffer();
            this.framebuffer = null;
        }
    }

    @Override
    public void render(LaptopTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState state = te.getBlockState();
        BlockState screenState = state.getBlock().getDefaultState().with(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);
        BlockPos pos = te.getPos();

        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        {
            GlStateManager.translated(x, y, z);

            GlStateManager.pushMatrix();
            {
                GlStateManager.translatef(0.5f, 0f, 0.5f);
                GlStateManager.rotatef(180f - state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle(), 0f, 1f, 0f);
                GlStateManager.translatef(-0.5f, 0f, -0.5f);
                GlStateManager.translatef(0f, 0.0625f, -0.25f);

                GlStateManager.translatef(0f, 0f, 1f);
                GlStateManager.rotatef(te.getScreenAngle(partialTicks), 1f, 0f, 0f);
                GlStateManager.translatef(0f, 0f, -1f);

                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRendererDispatcher blockrendererdispatcher = minecraft.getBlockRendererDispatcher();
                IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModelShapes().getModel(screenState);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(minecraft.world, ibakedmodel, screenState, pos, buffer, false, this.random, 0, EmptyModelData.INSTANCE);

                buffer.setTranslation(0.0D, 0.0D, 0.0D);
                tessellator.draw();
                GlStateManager.enableLighting();
            }
            GlStateManager.popMatrix();

            if (GLX.isUsingFBOs() && te.getScreenAngle(partialTicks) != 0)
            {
                GlStateManager.pushMatrix();
                {
                    //TODO add a setting to change the scale
                    float scale = 2f;

                    if (this.framebuffer == null)
                    {
                        this.framebuffer = new Framebuffer((int) (DeviceConstants.LAPTOP_SCREEN_WIDTH * scale), (int) (DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale), true, true);
                    }

                    GlStateManager.disableLighting();
                    GlStateManager.disableFog();
                    this.framebuffer.bindFramebuffer(true);
                    GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, true);

                    GlStateManager.viewport(0, 0, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight);
                    GlStateManager.matrixMode(GL_PROJECTION);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    GlStateManager.ortho(0.0D, this.framebuffer.framebufferWidth / scale, this.framebuffer.framebufferHeight / scale, 0.0D, 0.3D, 2000.0D);
                    GlStateManager.matrixMode(GL_MODELVIEW);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    GlStateManager.translatef(0.0F, 0.0F, -1000.0F);

                    RenderUtil.framebufferHeight = this.framebuffer.framebufferHeight;
                    RenderUtil.framebufferScale = scale;

                    {
                        GlStateManager.color4f(1, 1, 1, 1);
                        LaptopRenderer.render(te, minecraft, minecraft.fontRenderer, 0, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, -Integer.MAX_VALUE, -Integer.MAX_VALUE, partialTicks);
                    }

                    GlStateManager.matrixMode(GL_PROJECTION);
                    GlStateManager.popMatrix();
                    GlStateManager.matrixMode(GL_MODELVIEW);
                    GlStateManager.popMatrix();

                    minecraft.getFramebuffer().bindFramebuffer(true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableFog();

                    GlStateManager.translated(0.5, 0, 0.5);
                    GlStateManager.rotated(-state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle(), 0, 1, 0);
                    GlStateManager.translated(-0.5, 0, -0.5);

                    GlStateManager.translated(0, 0.0625, 4 * 0.0625);
                    GlStateManager.rotated(90 - te.getScreenAngle(partialTicks), 1, 0, 0);
                    GlStateManager.translated(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);

                    this.framebuffer.bindFramebufferTexture();

                    this.setLightmapDisabled(true);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, 1).endVertex();
                    buffer.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, 0, 0).tex(1, 0).normal(0, 0, 1).endVertex();
                    buffer.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(1, 1).normal(0, 0, 1).endVertex();
                    buffer.pos(0, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(0, 1).normal(0, 0, 1).endVertex();
                    tessellator.draw();
                    this.setLightmapDisabled(false);
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }
}
