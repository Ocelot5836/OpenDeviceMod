package com.ocelot.opendevices.core.render;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Models
{
    LAPTOP_SCREEN;

    private ResourceLocation location;

    Models()
    {
        this(null);
    }

    Models(ResourceLocation location)
    {
        this.location = location;
    }

    public IBakedModel getModel()
    {
        if (this.location == null)
            this.location = new ResourceLocation(OpenDevices.MOD_ID, "custom/" + this.name().toLowerCase(Locale.ROOT));
        return Minecraft.getInstance().getModelManager().getModel(this.location);
    }

    @SubscribeEvent
    public static void register(ModelRegistryEvent event)
    {
        for (Models model : Models.values())
        {
            ModelLoader.addSpecialModel(model.location);
        }
    }
}
