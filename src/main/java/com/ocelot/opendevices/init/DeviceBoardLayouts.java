package com.ocelot.opendevices.init;

import com.ocelot.opendevices.crafting.ComponentBuilderLayout;

@Deprecated
public class DeviceBoardLayouts
{
    public static final ComponentBuilderLayout CENTER = register("center", ComponentBuilderLayout.SLOT_4);
    public static final ComponentBuilderLayout TEST_CROSS = register("test_cross", ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_4 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_7);
    public static final ComponentBuilderLayout TEST5 = register("test5", ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_2 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_7);
    public static final ComponentBuilderLayout TEST9 = register("test9", ComponentBuilderLayout.SLOT_0 | ComponentBuilderLayout.SLOT_1 | ComponentBuilderLayout.SLOT_2 | ComponentBuilderLayout.SLOT_3 | ComponentBuilderLayout.SLOT_4 | ComponentBuilderLayout.SLOT_5 | ComponentBuilderLayout.SLOT_6 | ComponentBuilderLayout.SLOT_7 | ComponentBuilderLayout.SLOT_8);

    public static ComponentBuilderLayout register(String registryName, int activeSlots)
    {
        return ComponentBuilderLayout.EMPTY;
    }
}
