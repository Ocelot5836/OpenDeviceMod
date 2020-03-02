package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.registry.ComponentBuilderBoardLayout;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class DeviceBoardLayouts
{
    private static Set<ComponentBuilderBoardLayout> BOARD_LAYOUTS = new HashSet<>();

    public static final ComponentBuilderBoardLayout TEST_CROSS = register("test_cross", ComponentBuilderBoardLayout.SLOT_1 | ComponentBuilderBoardLayout.SLOT_3 | ComponentBuilderBoardLayout.SLOT_4 | ComponentBuilderBoardLayout.SLOT_5 | ComponentBuilderBoardLayout.SLOT_7);
    public static final ComponentBuilderBoardLayout TEST5 = register("test5", ComponentBuilderBoardLayout.SLOT_1 | ComponentBuilderBoardLayout.SLOT_2 | ComponentBuilderBoardLayout.SLOT_3 | ComponentBuilderBoardLayout.SLOT_5 | ComponentBuilderBoardLayout.SLOT_7);
    public static final ComponentBuilderBoardLayout TEST9 = register("test9", ComponentBuilderBoardLayout.SLOT_0 | ComponentBuilderBoardLayout.SLOT_1 | ComponentBuilderBoardLayout.SLOT_2 | ComponentBuilderBoardLayout.SLOT_3 | ComponentBuilderBoardLayout.SLOT_4 | ComponentBuilderBoardLayout.SLOT_5 | ComponentBuilderBoardLayout.SLOT_6 | ComponentBuilderBoardLayout.SLOT_7 | ComponentBuilderBoardLayout.SLOT_8);

    public static ComponentBuilderBoardLayout register(String registryName, int activeSlots)
    {
        ComponentBuilderBoardLayout layout = new ComponentBuilderBoardLayout(new ResourceLocation(OpenDevices.MOD_ID, "board_layout/" + registryName), activeSlots).setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName));
        BOARD_LAYOUTS.add(layout);
        return layout;
    }

    public static ComponentBuilderBoardLayout[] getBoardLayouts()
    {
        return BOARD_LAYOUTS.toArray(new ComponentBuilderBoardLayout[0]);
    }
}
