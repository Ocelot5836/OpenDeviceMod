package com.ocelot.opendevices.init;

import com.ocelot.opendevices.api.crafting.ComponentBuilderLayout;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public class DeviceBoardLayouts
{
    private static Set<ComponentBuilderLayout> BOARD_LAYOUTS = new HashSet<>();

    public static final ComponentBuilderLayout CENTER = register("center", ComponentBuilderLayout.SLOT_4);
    public static final ComponentBuilderLayout TEST_CROSS = register("test_cross", ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_4 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_7);
    public static final ComponentBuilderLayout TEST5 = register("test5", ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_2 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_7);
    public static final ComponentBuilderLayout TEST9 = register("test9", ComponentBuilderLayout.SLOT_0 | ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_2 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_4 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_6 | ComponentBuilderLayout.SLOT_7 | ComponentBuilderLayout.SLOT_8);

    public static ComponentBuilderLayout register(String registryName, int activeSlots)
    {
        ComponentBuilderLayout layout = null;//new ComponentBuilderLayout(new ResourceLocation(OpenDevices.MOD_ID, "board_layout/" + registryName), new ItemStack(Blocks.DIAMOND_ORE), activeSlots).setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, registryName));
        BOARD_LAYOUTS.add(layout);
        return layout;
    }

    public static ComponentBuilderLayout[] getBoardLayouts()
    {
        return BOARD_LAYOUTS.toArray(new ComponentBuilderLayout[0]);
    }
}
