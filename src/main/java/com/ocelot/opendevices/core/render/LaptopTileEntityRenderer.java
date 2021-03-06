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
import io.github.ocelot.client.FontHelper;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.framebuffer.AdvancedFbo;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID)
public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    public static final int MIN_RESOLUTION = 1;
    public static final int MAX_RESOLUTION = 8;
    public static final int MIN_SAMPLES = 1;
    public static final int MAX_SAMPLES = 16;

    private static final Set<LaptopTileEntity> LAPTOP_SCREENS = new HashSet<>();
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector4f POSITION = new Vector4f();
    private static AdvancedFbo msFramebuffer;
    private static AdvancedFbo framebuffer;

    public LaptopTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher)
    {
        super(rendererDispatcher);
    }

    private static void deleteFramebuffer()
    {
        if (msFramebuffer != null)
        {
            msFramebuffer.free();
            msFramebuffer = null;
        }
        if (framebuffer != null)
        {
            framebuffer.free();
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

    private static void initFramebuffers(Minecraft minecraft, int scale)
    {
        int samples = MathHelper.clamp(OpenDevicesConfig.CLIENT.laptopScreenSamples.get(), MIN_SAMPLES, MAX_SAMPLES);
        if (msFramebuffer == null)
        {
            msFramebuffer = new AdvancedFbo.Builder(DeviceConstants.LAPTOP_SCREEN_WIDTH * scale, DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale).addColorRenderBuffer(samples).setDepthRenderBuffer(samples).build();
            msFramebuffer.create();
        }

        if (framebuffer == null)
        {
            framebuffer = new AdvancedFbo.Builder(DeviceConstants.LAPTOP_SCREEN_WIDTH * scale, DeviceConstants.LAPTOP_SCREEN_HEIGHT * scale).addColorTextureBuffer(minecraft.gameSettings.mipmapLevels).build();
            framebuffer.create();
            framebuffer.getColorAttachment(0).bind();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        }
    }

    private static void renderLaptopScreen(Computer computer, MatrixStack matrixStack, Minecraft minecraft, BlockState state, double distance, float screenAngle, int combinedLight, float partialTicks)
    {
        if(distance >= OpenDevicesConfig.CLIENT.laptopScreenRenderRange.get() * OpenDevicesConfig.CLIENT.laptopScreenRenderRange.get())
            return;
        matrixStack.push();
        {
            int scale = MathHelper.clamp(OpenDevicesConfig.CLIENT.laptopScreenResolution.get(), MIN_RESOLUTION, MAX_RESOLUTION);

            initFramebuffers(minecraft, scale);

            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            msFramebuffer.bind(true);
            RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            RenderSystem.matrixMode(GL_PROJECTION);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, (float) msFramebuffer.getWidth() / (float) scale, (float) msFramebuffer.getHeight() / (float) scale, 0.0D, 0.3D, 20000.0D);
            RenderSystem.matrixMode(GL_MODELVIEW);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -1000.0F);

            ScissorHelper.framebufferHeight = msFramebuffer.getHeight();
            ScissorHelper.framebufferScale = scale;

            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                if (distance >= OpenDevicesConfig.CLIENT.laptopScreenScreensaverRange.get() * OpenDevicesConfig.CLIENT.laptopScreenScreensaverRange.get())
                {
                    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;// Minecraft.getInstance().getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer);
                    if (fontRenderer != null && Minecraft.getInstance().player != null)
                    {
                        String text = Minecraft.getInstance().player.getDisplayName().getFormattedText();
                        float textWidth = fontRenderer.getStringWidth(text);
                        float textScale = (float) msFramebuffer.getWidth() / textWidth;
                        RenderSystem.pushMatrix();
                        RenderSystem.translatef(0, msFramebuffer.getHeight() / 2f, 0);
                        RenderSystem.scalef(textScale, textScale, 1);
                        RenderSystem.translatef(textWidth / 2, -fontRenderer.FONT_HEIGHT / 2f, 0);
                        RenderSystem.rotatef((float) (Math.abs(Math.sin((Minecraft.getInstance().player.world.getGameTime() + partialTicks) / 12f)))*90, 0, 1, 0);
                        RenderSystem.translatef(-textWidth / 2, 0, 0);
                        FontHelper.drawString(fontRenderer, text, 0, 0, 0xffffffff, false);
                        RenderSystem.popMatrix();
                    }
                }
                else
                {
                    LaptopRenderer.render(computer, minecraft, minecraft.fontRenderer, 0, 0, DeviceConstants.LAPTOP_SCREEN_WIDTH, DeviceConstants.LAPTOP_SCREEN_HEIGHT, -Integer.MAX_VALUE, -Integer.MAX_VALUE, partialTicks);
                }
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish();
            }

            RenderSystem.matrixMode(GL_PROJECTION);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
            RenderSystem.popMatrix();

            RenderSystem.enableFog();
            RenderSystem.enableLighting();

            matrixStack.translate(0.5, 0, 0.5);
            matrixStack.rotate(Vector3f.YN.rotationDegrees(state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle()));
            matrixStack.translate(-0.5, 0, -0.5);

            matrixStack.translate(0, 0.0625, 4 * 0.0625);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90 - screenAngle));
            matrixStack.translate(2 * 0.0625, 2.75 * 0.0625, 0.125 * 0.0625);

            msFramebuffer.resolveToAdvancedFbo(framebuffer);
            framebuffer.getColorAttachment(0).bind();
            glGenerateMipmap(GL_TEXTURE_2D);

            minecraft.getFramebuffer().bindFramebuffer(true);

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
                renderLaptopScreen(te, matrixStack, minecraft, te.getBlockState(), pos.distanceSq(projectedView.getX(), projectedView.getY(), projectedView.getZ(), true), screenAngle, WorldRenderer.getCombinedLight(world, te.getPos()), partialTicks);
                matrixStack.pop();
            }
        }
        LAPTOP_SCREENS.clear();
    }

    public static void addReloadListener()
    {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) resourceManager).addReloadListener((ISelectiveResourceReloadListener) (__, resourcePredicate) ->
            {
                if (resourcePredicate.test(VanillaResourceType.TEXTURES))
                    reload();
            });
        }
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
