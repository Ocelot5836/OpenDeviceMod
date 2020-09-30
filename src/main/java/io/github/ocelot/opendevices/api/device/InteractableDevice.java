package io.github.ocelot.opendevices.api.device;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.core.init.DeviceMessages;
import io.github.ocelot.opendevices.core.network.play.handler.SOpenDeviceMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>A {@link Device} that can be interacted with by a player.</p>
 *
 * @author Ocelot
 */
public interface InteractableDevice extends Device
{
    /**
     * Attempts to have the player interact with the specified device.
     *
     * @param device The device to interact with
     * @param player The player doing the action
     * @return Whether or not the player was able to interact with the device
     */
    static boolean attemptInteract(InteractableDevice device, ServerPlayerEntity player)
    {
        PlayerEntity userPlayer = device.getUser();
        if (userPlayer != null)
        {
            player.sendStatusMessage(new TranslationTextComponent("block." + OpenDevices.MOD_ID + ".device.using", userPlayer.getDisplayName()), true);
            return false;
        }

        if (device.canInteract(player))
        {
            UUID address = device.getAddress();
            Optional<CompoundNBT> deviceData = DeviceManager.get(player.getServerWorld()).getData(address);

            if (!deviceData.isPresent())
                return false;

            if (device.startInteracting(player))
            {
                DeviceMessages.PLAY.send(PacketDistributor.PLAYER.with(() -> player), new SOpenDeviceMessage(address, deviceData.get()));
                return true;
            }
        }

        return false;
    }

    /**
     * Starts an interaction between this device and the specified player server side.
     *
     * @param player The player to start interacting with
     * @return Whether or not the player successfully interacted with this device
     */
    boolean startInteracting(PlayerEntity player);

    /**
     * Stops interacting with the specified player server side.
     *
     * @param player The player to stop interacting with
     */
    void stopInteracting(ServerPlayerEntity player);

    /**
     * Checks to see if the specified player is able to interact with this device.
     *
     * @param player The player to check
     * @return Whether or not the player has the capability to use this device
     */
    boolean canInteract(PlayerEntity player);

    /**
     * @return The current player using this device or <code>null</code> if no players are using
     */
    @Nullable
    PlayerEntity getUser();
}
