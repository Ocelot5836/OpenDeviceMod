package io.github.ocelot.opendevices.core.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.device.Computer;
import io.github.ocelot.opendevices.core.init.DeviceMessages;
import io.github.ocelot.opendevices.core.network.play.handler.CCloseDeviceMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class LaptopScreen extends Screen
{
    private final Computer computer;

    public LaptopScreen(Computer computer)
    {
        super(new TranslationTextComponent("screen." + OpenDevices.MOD_ID + ".laptop"));
        this.computer = computer;
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void onClose()
    {
        DeviceMessages.PLAY.send(PacketDistributor.SERVER.noArg(), new CCloseDeviceMessage(this.computer.getAddress()));
    }
}
