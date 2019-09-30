package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
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
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    public static final LaptopTileEntityRenderer INSTANCE = new LaptopTileEntityRenderer();

    private Random random;
    private Framebuffer framebuffer;
    private int screenList;

    private LaptopTileEntityRenderer()
    {
        this.random = new Random();
        this.framebuffer = null;
        this.screenList = -1;
    }

    private void createScreenList()
    {
        this.screenList = GlStateManager.genLists(1);
        GlStateManager.newList(this.screenList, GL11.GL_COMPILE);
        {
            this.framebuffer.bindFramebufferTexture();

            this.setLightmapDisabled(true);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
            buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, 1).endVertex();
            buffer.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, 0, 0).tex(1, 0).normal(0, 0, 1).endVertex();
            buffer.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(1, 1).normal(0, 0, 1).endVertex();
            buffer.pos(0, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(0, 1).normal(0, 0, 1).endVertex();
            tessellator.draw();
            this.setLightmapDisabled(false);
        }
        GlStateManager.endList();
    }

    public void delete()
    {
        OpenDevices.LOGGER.debug("Deleting Laptop Render Cache");

        this.framebuffer.deleteFramebuffer();
        this.framebuffer = null;
        if (this.screenList != -1)
        {
            GlStateManager.deleteLists(this.screenList, 1);
            this.screenList = -1;
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
                GlStateManager.translated(0.5, 0, 0.5);
                GlStateManager.rotated(180 - state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle(), 0, 1, 0);
                GlStateManager.translated(-0.5, 0, -0.5);
                GlStateManager.translated(0, 0.0625, -0.25);

                GlStateManager.translated(0, 0, 1);
                GlStateManager.rotated(te.getScreenAngle(partialTicks), 1, 0, 0);
                GlStateManager.translated(0, 0, -1);

                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRendererDispatcher blockrendererdispatcher = minecraft.getBlockRendererDispatcher();
                IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModelShapes().getModel(screenState);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(minecraft.world, ibakedmodel, screenState, pos, buffer, false, this.random, 0, EmptyModelData.INSTANCE);

                buffer.setTranslation(0.0D, 0.0D, 0.0D);
                tessellator.draw();
                GlStateManager.enableLighting();
            }
            GlStateManager.popMatrix();

            if (te.getScreenAngle(partialTicks) != 0)
            {
                GlStateManager.pushMatrix();
                {
                    if (this.framebuffer == null)
                    {
                        this.framebuffer = new Framebuffer(DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, true, true);
                        this.framebuffer.checkFramebufferComplete();
                    }

                    GlStateManager.disableLighting();
                    GlStateManager.disableFog();
                    this.framebuffer.bindFramebuffer(true);
                    GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

                    GlStateManager.viewport(0, 0, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight);
                    GlStateManager.matrixMode(GL11.GL_PROJECTION);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    GlStateManager.ortho(0.0D, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight, 0.0D, 1000.0D, 3000.0D);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    GlStateManager.translatef(0.0F, 0.0F, -2000.0F);

                    {
                        GlStateManager.color4f(1, 1, 1, 1);
                        LaptopRenderer.render(te, minecraft, minecraft.fontRenderer, 0, 0, -1, -1, partialTicks);
                    }

                    GlStateManager.matrixMode(GL11.GL_PROJECTION);
                    GlStateManager.popMatrix();
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                    GlStateManager.popMatrix();

                    minecraft.getFramebuffer().bindFramebuffer(true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableFog();

                    //                if (this.screenList == -1)
                    //                {
                    //                    this.createScreenList();
                    //                }
                    //                GlStateManager.callList(this.screenList);

                    GlStateManager.translated(0.5, 0, 0.5);
                    GlStateManager.rotated(-state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle(), 0, 1, 0);
                    GlStateManager.translated(-0.5, 0, -0.5);

                    GlStateManager.translated(0, 0.0625, 4 * 0.0625);
                    GlStateManager.rotated(90 - te.getScreenAngle(partialTicks), 1, 0, 0);
                    GlStateManager.translated(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);

                    GlStateManager.callList(this.screenList);
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }
}
