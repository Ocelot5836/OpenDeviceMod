package com.ocelot.opendevices;

import com.ocelot.opendevices.core.render.LaptopTileEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * <p>Contains the base mod configurations.</p>
 *
 * @author Ocelot
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = OpenDevices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OpenDevicesConfig
{
    public static final Client CLIENT;
    //    public static final Common COMMON;

    private static final ForgeConfigSpec clientSpec;
    //    private static final ForgeConfigSpec commonSpec;

    static
    {
        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        //        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        //        commonSpec = commonSpecPair.getRight();
        //        COMMON = commonSpecPair.getLeft();
    }

    public static void init(ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.CLIENT, clientSpec);
        //        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
    }

    @SubscribeEvent
    public static void onConfigReloaded(ModConfig.Reloading event)
    {
        if (OpenDevices.MOD_ID.equals(event.getConfig().getModId()))
        {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> LaptopTileEntityRenderer::reload);
        }
    }

    public static class Client
    {
        public final ForgeConfigSpec.BooleanValue drawLaptopScreens;
        public final ForgeConfigSpec.IntValue laptopScreenResolution;
        public final ForgeConfigSpec.IntValue laptopScreenSamples;
        public final ForgeConfigSpec.DoubleValue laptopScreenScreensaverRange;
        public final ForgeConfigSpec.DoubleValue laptopScreenRenderRange;

        private Client(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client configuration settings").push("client").push("laptop").comment("Laptop Rendering Settings");
            this.drawLaptopScreens = builder
                    .comment("Draws the contents of the Laptop into the world. May be laggy for older computers.")
                    .translation("config." + OpenDevices.MOD_ID + ".client.laptop.drawLaptopScreens")
                    .define("drawLaptopScreens", true);
            this.laptopScreenResolution = builder
                    .comment("Specifies the resolution of the Laptop screen in the world. Higher values cause more lag.")
                    .translation("config." + OpenDevices.MOD_ID + ".client.laptop.laptopScreenResolution")
                    .defineInRange("laptopScreenResolution", 1, LaptopTileEntityRenderer.MIN_RESOLUTION, LaptopTileEntityRenderer.MAX_RESOLUTION);
            this.laptopScreenSamples = builder
                    .comment("Specifies the amount of samples to use for Anti-Aliasing. Higher values cause more lag.")
                    .translation("config." + OpenDevices.MOD_ID + ".client.laptop.laptopScreenSamples")
                    .defineInRange("laptopScreenSamples", 1, LaptopTileEntityRenderer.MIN_SAMPLES, LaptopTileEntityRenderer.MAX_SAMPLES);
            this.laptopScreenScreensaverRange = builder
                    .comment("Specifies how many blocks away laptop screens should render screensavers for.")
                    .translation("config." + OpenDevices.MOD_ID + ".client.laptop.laptopScreenScreensaverRange")
                    .defineInRange("laptopScreenScreensaverRange", 3.0, 0.0, 64.0);
            this.laptopScreenRenderRange = builder
                    .comment("Specifies how many blocks away laptop screens should render for.")
                    .translation("config." + OpenDevices.MOD_ID + ".client.laptop.laptopScreenRenderRange")
                    .defineInRange("laptopScreenRenderRange", 10.0, 0.0, 64.0);
            builder.pop();
        }
    }

    public static class Common
    {
        private Common(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Server configuration settings").push("server");
            builder.pop();
        }
    }
}
