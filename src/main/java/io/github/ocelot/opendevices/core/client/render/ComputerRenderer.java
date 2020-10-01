package io.github.ocelot.opendevices.core.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.opendevices.api.computer.Computer;

public class ComputerRenderer
{
    public static void tick(Computer computer)
    {

    }

    public static void render(Computer computer, MatrixStack matrixStack)
    {
        int screenWidth = computer.getScreenWidth();
        int screenHeight = computer.getScreenHeight();
    }
}
