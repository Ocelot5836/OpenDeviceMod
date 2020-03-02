package com.ocelot.opendevices.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ComponentBuilderBoardLayout extends ForgeRegistryEntry<ComponentBuilderBoardLayout>
{
    public static final int SLOT_0 = 0x01;
    public static final int SLOT_1 = 0x02;
    public static final int SLOT_2 = 0x04;
    public static final int SLOT_3 = 0x08;
    public static final int SLOT_4 = 0x10;
    public static final int SLOT_5 = 0x20;
    public static final int SLOT_6 = 0x40;
    public static final int SLOT_7 = 0x80;
    public static final int SLOT_8 = 0x100;

    private ResourceLocation textureLocation;
    private int slotsUsed;

    public ComponentBuilderBoardLayout(ResourceLocation textureLocation, int slotsUsed)
    {
        this.textureLocation = textureLocation;
        this.slotsUsed = slotsUsed;
    }

    public ResourceLocation getTextureLocation()
    {
        return textureLocation;
    }

    public boolean hasSlot(int slot)
    {
        return (this.slotsUsed & slot) > 0;
    }
}
