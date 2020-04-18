package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.block.DeviceBlock;
import com.ocelot.opendevices.core.LaptopTileEntity;
import io.github.ocelot.client.ScissorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.BlockPos;

import static org.lwjgl.opengl.GL30.*;

public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    private static Framebuffer framebuffer;

    public LaptopTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher)
    {
        super(rendererDispatcher);
    }

    public static void delete()
    {
        OpenDevices.LOGGER.debug("Deleting Laptop Render Cache");

        if (framebuffer != null)
        {
            framebuffer.deleteFramebuffer();
            framebuffer = null;
        }
    }

    @Override
    public void render(LaptopTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState state = te.getBlockState();
        IBakedModel screenModel = Models.LAPTOP_SCREEN.getModel();
        BlockPos pos = te.getPos();

        matrixStack.push();
        {
            matrixStack.translate(0.5f, 0f, 0.5f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180f - state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle()));
            matrixStack.translate(-0.5f, 0f, -0.5f);
            matrixStack.translate(0f, 0.0625f, -0.25f);

            matrixStack.translate(0f, 0f, 1f);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(te.getScreenAngle(partialTicks)));
            matrixStack.translate(0f, 0f, -1f);

            //                Tessellator tessellator = Tessellator.getInstance();
            //                BufferBuilder buffer = tessellator.getBuffer();
            //                buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
            //                buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
            //
            //                BlockRendererDispatcher blockrendererdispatcher = minecraft.getBlockRendererDispatcher();
            //                IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModelShapes().getModel(screenState);
            //                blockrendererdispatcher.getBlockModelRenderer().renderModel(minecraft.world, ibakedmodel, screenState, pos, buffer, false, this.getWorld().getRandom(), 0, EmptyModelData.INSTANCE);
            //
            //                buffer.setTranslation(0.0D, 0.0D, 0.0D);
            //                tessellator.draw();
        }
        matrixStack.pop();

        if (te.getScreenAngle(partialTicks) != 0)
        {
            matrixStack.push();
            {
                //TODO add a setting to change the scale
                float scale = 2f;

                if (framebuffer == null)
                {
                    framebuffer = new Framebuffer((int) (DeviceConstants.LAPTOP_SCREEN_WIDTH * scale), (int) (DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale), true, true);
                }

                RenderSystem.disableLighting();
                RenderSystem.disableFog();
                framebuffer.bindFramebuffer(true);
                GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, true);

                RenderSystem.viewport(0, 0, framebuffer.framebufferWidth, framebuffer.framebufferHeight);
                RenderSystem.matrixMode(GL_PROJECTION);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.ortho(0.0D, framebuffer.framebufferWidth / scale, framebuffer.framebufferHeight / scale, 0.0D, 0.3D, 2000.0D);
                RenderSystem.matrixMode(GL_MODELVIEW);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.translatef(0.0F, 0.0F, -1000.0F);

                ScissorHelper.framebufferHeight = framebuffer.framebufferHeight;
                ScissorHelper.framebufferScale = scale;

                {
                    RenderSystem.color4f(1, 1, 1, 1);
                    // TODO render something instead of the laptop when too far away (screensaver maybe?)
                    LaptopRenderer.render(te, minecraft, minecraft.fontRenderer, 0, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, -Integer.MAX_VALUE, -Integer.MAX_VALUE, partialTicks);
                }

                RenderSystem.matrixMode(GL_PROJECTION);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(GL_MODELVIEW);
                RenderSystem.popMatrix();

                minecraft.getFramebuffer().bindFramebuffer(true);
                RenderSystem.enableFog();
                RenderSystem.enableLighting();

                matrixStack.translate(0.5, 0, 0.5);
                matrixStack.rotate(Vector3f.YN.rotationDegrees(state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle()));
                matrixStack.translate(-0.5, 0, -0.5);

                matrixStack.translate(0, 0.0625, 4 * 0.0625);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(90 - te.getScreenAngle(partialTicks)));
                matrixStack.translate(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);

                framebuffer.bindFramebufferTexture();

                // TODO fix this class

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                builder.begin(7, DefaultVertexFormats.POSITION_TEX_LIGHTMAP_COLOR);
                builder.pos(0, 0, 0).tex(0, 0).lightmap(combinedLight & 0xF).color(1, 1, 1, 1).endVertex();
                builder.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, 0, 0).tex(1, 0).lightmap(combinedLight & 0xF).color(1, 1, 1, 1).endVertex();
                builder.pos(DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(1, 1).lightmap(combinedLight & 0xF).color(1, 1, 1, 1).endVertex();
                builder.pos(0, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0).tex(0, 1).lightmap(combinedLight & 0xF).color(1, 1, 1, 1).endVertex();
                tessellator.draw();
            }
            matrixStack.pop();
        }
    }
}
