package io.github.ocelot.opendevices.core.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.computer.Computer;
import io.github.ocelot.opendevices.core.client.render.ComputerRenderer;
import io.github.ocelot.opendevices.core.init.DeviceMessages;
import io.github.ocelot.opendevices.core.network.play.handler.CCloseDeviceMessage;
import io.github.ocelot.sonar.client.render.ShapeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class LaptopScreen extends Screen
{
    public static final int BORDER_SIZE = 10;
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/laptop.png");

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
        partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        this.renderBackground(matrixStack);

        int left = (this.width - this.computer.getScreenWidth()) / 2 - BORDER_SIZE;
        int top = (this.height - this.computer.getScreenHeight()) / 2 - BORDER_SIZE;

        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_LOCATION);
        renderExpanding(matrixStack, left, top, 0, 0, this.computer.getScreenWidth() + BORDER_SIZE * 2, this.computer.getScreenHeight() + BORDER_SIZE * 2, 10);

        matrixStack.push();
        matrixStack.translate(left + BORDER_SIZE, top + BORDER_SIZE, 0);
        ComputerRenderer.render(this.computer, matrixStack);
        matrixStack.pop();

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

    private static void renderExpanding(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int cellSize)
    {
        IVertexBuilder builder = ShapeRenderer.begin();
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x, y, u, v, cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x, y + cellSize, u, v + cellSize, cellSize, height - 2 * cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x, y + height - cellSize, u, v + 2 * cellSize, cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + cellSize, y, u + cellSize, v, width - 2 * cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + cellSize, y + cellSize, u + cellSize, v + cellSize, width - 2 * cellSize, height - 2 * cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + cellSize, y + height - cellSize, u + cellSize, v + 2 * cellSize, width - 2 * cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + width - cellSize, y, u + 2 * cellSize, v, cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + width - cellSize, y + cellSize, u + 2 * cellSize, v + cellSize, cellSize, height - 2 * cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.drawRectWithTexture(builder, matrixStack, x + width - cellSize, y + height - cellSize, u + 2 * cellSize, v + 2 * cellSize, cellSize, cellSize, cellSize, cellSize, 64, 64);
        ShapeRenderer.end();
    }
}
