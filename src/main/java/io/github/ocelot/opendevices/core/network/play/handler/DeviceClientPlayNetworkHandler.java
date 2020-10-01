package io.github.ocelot.opendevices.core.network.play.handler;

import io.github.ocelot.opendevices.api.computer.Computer;
import io.github.ocelot.opendevices.api.device.DeviceManager;
import io.github.ocelot.opendevices.core.client.screen.LaptopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeviceClientPlayNetworkHandler implements IDeviceClientPlayNetworkHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void processOpenDeviceScreen(SOpenDeviceMessage msg, NetworkEvent.Context ctx)
    {
        World world = Minecraft.getInstance().world;
        if (world == null)
            return;

        DeviceManager.<Computer>readDevice(world, msg.getAddress(), msg.getData()).ifPresent(device -> Minecraft.getInstance().displayGuiScreen(new LaptopScreen(device)));
        // TODO allow other devices to easily open a screen
    }
}
