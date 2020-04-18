package com.ocelot.opendevices.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Model
{
    LAPTOP_SCREEN;

    private static final Random RANDOM = new Random();

    private ResourceLocation location;

    Model()
    {
        this(null);
    }

    Model(ResourceLocation location)
    {
        this.location = location;
    }

    public ResourceLocation getLocation()
    {
        if (this.location == null)
            this.location = new ResourceLocation(OpenDevices.MOD_ID, "custom/" + this.name().toLowerCase(Locale.ROOT));
        return this.location;
    }

    public IBakedModel getModel()
    {
        return Minecraft.getInstance().getModelManager().getModel(this.getLocation());
    }

    @SubscribeEvent
    public static void register(ModelRegistryEvent event)
    {
        for (Model model : Model.values())
        {
            ModelLoader.addSpecialModel(model.getLocation());
        }
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, IModelData modelData)
    {
        this.render(matrixStack, buffer.getBuffer(Atlases.getTranslucentCullBlockType()), combinedLight, combinedOverlay, modelData);
    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int combinedLight, int combinedOverlay, IModelData modelData)
    {
        for (Direction direction : Direction.values())
        {
            RANDOM.setSeed(42L);
            render(matrixStack, buffer, this.getModel().getQuads(null, direction, RANDOM, modelData), combinedLight, combinedOverlay);
        }

        RANDOM.setSeed(42L);
        render(matrixStack, buffer, this.getModel().getQuads(null, null, RANDOM, modelData), combinedLight, combinedOverlay);
    }

    private static void render(MatrixStack matrixStack, IVertexBuilder buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay)
    {
        MatrixStack.Entry stackLast = matrixStack.getLast();
        for (BakedQuad bakedquad : quads)
        {
            buffer.addVertexData(stackLast, bakedquad, 1, 1, 1, combinedLight, combinedOverlay, true);
        }
    }
}
