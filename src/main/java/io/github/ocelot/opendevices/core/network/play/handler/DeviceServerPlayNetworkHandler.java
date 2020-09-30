package io.github.ocelot.opendevices.core.network.play.handler;

import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.api.device.DeviceManager;
import io.github.ocelot.opendevices.api.device.InteractableDevice;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class DeviceServerPlayNetworkHandler implements IDeviceServerPlayNetworkHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void processCloseDevice(CCloseDeviceMessage msg, NetworkEvent.Context ctx)
    {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null)
            return;

        Optional<Device> deviceOptional = DeviceManager.get(player.getServerWorld()).getDevice(msg.getAddress());
        if (!deviceOptional.isPresent())
        {
            LOGGER.warn("Failed to close unknown device with address '" + msg.getAddress() + "'.");
            return;
        }

        Device device = deviceOptional.get();
        if (!(device instanceof InteractableDevice))
        {
            LOGGER.warn("Device with address '" + msg.getAddress() + "' cannot be interacted with.");
            return;
        }

        ctx.enqueueWork(() -> ((InteractableDevice) device).stopInteracting(player));
        // TODO update listeners
    }
}
