package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.Random;

public class LaptopTileEntityRenderer extends TileEntityRenderer<LaptopTileEntity>
{
    public static final LaptopTileEntityRenderer INSTANCE = new LaptopTileEntityRenderer();

    private Random random = new Random();

    private LaptopTileEntityRenderer() {}

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
                GlStateManager.translated(0, 0.0625, 0.25);
                GlStateManager.rotated(te.getScreenAngle(partialTicks), 1, 0, 0);
                GlStateManager.translated(0, -0.0625, -0.25);

                GlStateManager.translated(0.5, 0, 0.5);
                GlStateManager.rotated(180 - state.get(DeviceBlock.HORIZONTAL_FACING).getHorizontalAngle(), 0, 1, 0);
                GlStateManager.translated(-0.5, 0, -0.5);
                GlStateManager.translated(0, 0.0625, -0.25);

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

        }
        GlStateManager.popMatrix();
    }
}
