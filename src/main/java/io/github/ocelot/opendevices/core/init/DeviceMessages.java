package io.github.ocelot.opendevices.core.init;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.core.network.play.handler.CCloseDeviceMessage;
import io.github.ocelot.opendevices.core.network.play.handler.DeviceClientPlayNetworkHandler;
import io.github.ocelot.opendevices.core.network.play.handler.DeviceServerPlayNetworkHandler;
import io.github.ocelot.opendevices.core.network.play.handler.SOpenDeviceMessage;
import io.github.ocelot.sonar.common.network.SonarNetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * @author Ocelot
 */
public class DeviceMessages
{
    public static final String VERSION = "1";
    public static final SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(OpenDevices.MOD_ID, "play"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static final SonarNetworkManager PLAY_HANDLER = new SonarNetworkManager(PLAY, () -> DeviceClientPlayNetworkHandler::new, () -> DeviceServerPlayNetworkHandler::new);

    public static void init()
    {
        PLAY_HANDLER.register(SOpenDeviceMessage.class, SOpenDeviceMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        PLAY_HANDLER.register(CCloseDeviceMessage.class, CCloseDeviceMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
