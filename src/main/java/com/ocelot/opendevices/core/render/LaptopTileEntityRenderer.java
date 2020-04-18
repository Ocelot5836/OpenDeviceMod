package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.block.DeviceBlock;
import com.ocelot.opendevices.core.LaptopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.model.data.EmptyModelData;

public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector4f POSITION = new Vector4f();
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
        Model screenModel = Model.LAPTOP_SCREEN;

        matrixStack.push();
        {
            matrixStack.translate(0.5, 0, 0.5);
            matrixStack.rotate(Vector3f.YN.rotationDegrees(state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle()));
            matrixStack.translate(-0.5, 0, -0.5);
            matrixStack.translate(0f, 0.0625, 0.25);
            matrixStack.rotate(Vector3f.XN.rotationDegrees(te.getScreenAngle(partialTicks)));

            screenModel.render(matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
        }
        matrixStack.pop();

        // TODO fix screen rendering
        //        if (te.getScreenAngle(partialTicks) != 0)
        //        {
        //            matrixStack.push();
        //            {
        //                //TODO add a setting to change the scale
        //                float scale = 2f;
        //
        //                if (framebuffer == null)
        //                {
        //                    framebuffer = new Framebuffer((int) (DeviceConstants.LAPTOP_SCREEN_WIDTH * scale), (int) (DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale), true, Minecraft.IS_RUNNING_ON_MAC);
        //                }
        //
        //                RenderSystem.disableLighting();
        //                RenderSystem.disableFog();
        //                framebuffer.bindFramebuffer(true);
        //                GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        //
        //                RenderSystem.matrixMode(GL_PROJECTION);
        //                RenderSystem.pushMatrix();
        //                RenderSystem.loadIdentity();
        //                RenderSystem.ortho(0.0D, framebuffer.framebufferWidth / scale, framebuffer.framebufferHeight / scale, 0.0D, 0.3D, 20000.0D);
        //                RenderSystem.matrixMode(GL_MODELVIEW);
        //                RenderSystem.pushMatrix();
        //                RenderSystem.loadIdentity();
        //                RenderSystem.translatef(0.0F, 0.0F, -1000.0F);
        //
        //                ScissorHelper.framebufferHeight = framebuffer.framebufferHeight;
        //                ScissorHelper.framebufferScale = scale;
        //
        //                {
        //                    RenderSystem.color4f(1, 1, 1, 1);
        //                    // TODO render something instead of the laptop when too far away (screensaver maybe?)
        //                    if(Minecraft.getInstance().ingameGUI != null)
        //                        Minecraft.getInstance().ingameGUI.renderGameOverlay(partialTicks);
        //                    LaptopRenderer.render(te, minecraft, minecraft.fontRenderer, 0, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, -Integer.MAX_VALUE, -Integer.MAX_VALUE, partialTicks);
        //                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish();
        //                }
        //
        //                RenderSystem.matrixMode(GL_PROJECTION);
        //                RenderSystem.popMatrix();
        //                RenderSystem.matrixMode(GL_MODELVIEW);
        //                RenderSystem.popMatrix();
        //
        //                minecraft.getFramebuffer().bindFramebuffer(true);
        //                RenderSystem.enableFog();
        //                RenderSystem.enableLighting();
        //
        //                matrixStack.translate(0.5, 0, 0.5);
        //                matrixStack.rotate(Vector3f.YN.rotationDegrees(state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle()));
        //                matrixStack.translate(-0.5, 0, -0.5);
        //
        //                matrixStack.translate(0, 0.0625, 4 * 0.0625);
        //                matrixStack.rotate(Vector3f.XP.rotationDegrees(90 - te.getScreenAngle(partialTicks)));
        //                matrixStack.translate(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);
        //
        //                framebuffer.bindFramebufferTexture();
        //
        //                MatrixStack.Entry matrixStackLast = matrixStack.getLast();
        //                Tessellator tessellator = Tessellator.getInstance();
        //                BufferBuilder builder = tessellator.getBuffer();
        //                builder.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
        //
        //                addVertex(builder, matrixStackLast, 0, 0, 0, 0, 0, combinedLight);
        //                addVertex(builder, matrixStackLast, DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, 0, 1, 0, 0, combinedLight);
        //                addVertex(builder, matrixStackLast, DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 1, 1, 0, combinedLight);
        //                addVertex(builder, matrixStackLast, 0, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0, 1, 0, combinedLight);
        //
        //                tessellator.draw();
        //            }
        //            matrixStack.pop();
        //        }
    }

    private static void addVertex(IVertexBuilder builder, MatrixStack.Entry entry, float x, float y, float u, float v, float fade, int combinedLight)
    {
        POSITION.set(x, y, (float) 0, 1);
        POSITION.transform(entry.getMatrix());
        NORMAL.set(0, 0, -1);
        NORMAL.transform(entry.getNormal());

        builder.pos(POSITION.getX(), POSITION.getY(), POSITION.getZ()).color(1, 1, 1, Math.max(0, 1 - fade)).tex(u, v).lightmap(combinedLight).normal(NORMAL.getX(), NORMAL.getY(), NORMAL.getZ()).endVertex();
    }
}
