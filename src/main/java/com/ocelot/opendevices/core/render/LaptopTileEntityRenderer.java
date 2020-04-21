package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.OpenDevicesConfig;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.block.DeviceBlock;
import com.ocelot.opendevices.core.LaptopTileEntity;
import io.github.ocelot.client.ScissorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID)
public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    public static final int MIN_RESOLUTION = 1;
    public static final int MAX_RESOLUTION = 16;

    private static final Set<LaptopTileEntity> LAPTOP_SCREENS = new HashSet<>();
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector4f POSITION = new Vector4f();
    private static Framebuffer framebuffer;

    public LaptopTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher)
    {
        super(rendererDispatcher);
    }

    private static void deleteFramebuffer()
    {
        if (framebuffer != null)
        {
            framebuffer.deleteFramebuffer();
            framebuffer = null;
        }
        LAPTOP_SCREENS.clear();
    }

    @SubscribeEvent
    public static void delete(WorldEvent.Unload event)
    {
        OpenDevices.LOGGER.debug("Deleting Laptop Render Cache");
        reload();
    }

    public static void reload()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(LaptopTileEntityRenderer::deleteFramebuffer);
        }
        else
        {
            deleteFramebuffer();
        }
    }

    private static void renderLaptopScreen(Computer computer, MatrixStack matrixStack, Minecraft minecraft, BlockState state, float screenAngle, int combinedLight, float partialTicks)
    {
        matrixStack.push();
        {
            int scale = MathHelper.clamp(OpenDevicesConfig.CLIENT.laptopScreenResolution.get(), MIN_RESOLUTION, MAX_RESOLUTION);

            if (framebuffer == null)
            {
                framebuffer = new Framebuffer(DeviceConstants.LAPTOP_SCREEN_WIDTH * scale, DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale, true, Minecraft.IS_RUNNING_ON_MAC);
            }

            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            framebuffer.bindFramebuffer(true);
            RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            RenderSystem.matrixMode(GL_PROJECTION);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, (float) framebuffer.framebufferWidth / (float) scale, (float) framebuffer.framebufferHeight / (float) scale, 0.0D, 0.3D, 20000.0D);
            RenderSystem.matrixMode(GL_MODELVIEW);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -1000.0F);

            ScissorHelper.framebufferHeight = framebuffer.framebufferHeight;
            ScissorHelper.framebufferScale = scale;

            {
                RenderSystem.color4f(1, 1, 1, 1);
                // TODO render something instead of the laptop when too far away (screensaver maybe?)
                LaptopRenderer.render(computer, minecraft, minecraft.fontRenderer, 0, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, -Integer.MAX_VALUE, -Integer.MAX_VALUE, partialTicks);
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish();
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
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90 - screenAngle));
            matrixStack.translate(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);

            framebuffer.bindFramebufferTexture();

            MatrixStack.Entry matrixStackLast = matrixStack.getLast();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(GL_QUADS, DefaultVertexFormats.BLOCK);

            addVertex(builder, matrixStackLast, 0, 0, 0, 0, 0, combinedLight);
            addVertex(builder, matrixStackLast, DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, 0, 1, 0, 0, combinedLight);
            addVertex(builder, matrixStackLast, DeviceConstants.LAPTOP_TE_SCREEN_WIDTH, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 1, 1, 0, combinedLight);
            addVertex(builder, matrixStackLast, 0, DeviceConstants.LAPTOP_TE_SCREEN_HEIGHT, 0, 1, 0, combinedLight);

            RenderSystem.enableDepthTest();
            tessellator.draw();
            RenderSystem.disableDepthTest();
        }
        matrixStack.pop();
    }

    @SubscribeEvent
    public static void onRenderLastEvent(RenderWorldLastEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.world;
        MatrixStack matrixStack = event.getMatrixStack();
        float partialTicks = event.getPartialTicks();

        if (world != null)
        {
            for (LaptopTileEntity te : LAPTOP_SCREENS)
            {
                float screenAngle = te.getScreenAngle(partialTicks);
                if (screenAngle == 0)
                    continue;
                Vec3d projectedView = minecraft.getRenderManager().info.getProjectedView();
                BlockPos pos = te.getPos();

                matrixStack.push();
                matrixStack.translate((double) pos.getX() - projectedView.getX(), (double) pos.getY() - projectedView.getY(), (double) pos.getZ() - projectedView.getZ());
                renderLaptopScreen(te, matrixStack, minecraft, te.getBlockState(), screenAngle, WorldRenderer.getCombinedLight(world, te.getPos()), partialTicks);
                matrixStack.pop();
            }
        }
        LAPTOP_SCREENS.clear();
    }

    @Override
    public void render(LaptopTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (OpenDevicesConfig.CLIENT.drawLaptopScreens.get() && te.getScreenAngle(partialTicks) != 0)
            LAPTOP_SCREENS.add(te);

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
    }

    private static void addVertex(IVertexBuilder builder, MatrixStack.Entry entry, float x, float y, float u, float v, float fade, int combinedLight)
    {
        builder.pos(entry.getMatrix(), x, y, 0).color(1, 1, 1, Math.max(0, 1 - fade)).tex(u, v).lightmap(combinedLight).normal(entry.getNormal(), 0, 1, 0).endVertex();
    }
}
