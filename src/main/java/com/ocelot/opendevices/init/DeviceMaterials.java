package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.item.ItemGroup;

public enum DeviceMaterials
{
    UNREFINED_PLASTIC("unrefined_plastic"),
    PLASTIC("plastic"),
    PLASTIC_FRAME("plastic_frame"),
    BATTERY("battery"),
    SCREEN("screen"),

    WHEEL("wheel"),
    SMALL_ELECTRIC_MOTOR("small_electric_motor"),
    CARRIAGE("carriage"),

    FLASH_CHIP("flash_chip"),
    CONTROLLER_CHIP("controller_chip"),

    // TODO add colors to drives
    HARD_DRIVE("hard_drive"),
    SOLID_STATE_DRIVE("solid_state_drive"),
    FLOPPY_DRIVE("floppy_drive"), // Placeholder
    FLASH_DRIVE("flash_drive"), // Placeholder

    FIBERGLASS_PANEL("fiberglass_panel"),
    GREEN_CIRCUIT_BOARD("green_circuit_board"),
    BLUE_CIRCUIT_BOARD("blue_circuit_board"),
    RED_CIRCUIT_BOARD("red_circuit_board"),

    ;

    /*
        COMPONENT_MOTHERBOARD = new ItemMotherboard();
        COMPONENT_CPU = new ItemMotherboard.Component("cpu");
        COMPONENT_RAM = new ItemMotherboard.Component("ram");
        COMPONENT_GPU = new ItemMotherboard.Component("gpu");
        COMPONENT_WIFI = new ItemMotherboard.Component("wifi");
     */

    private String registryName;
    private ItemGroup group;

    DeviceMaterials(String registryName)
    {
        this(registryName, OpenDevices.TAB);
    }

    DeviceMaterials(String registryName, ItemGroup group)
    {
        this.registryName = registryName;
        this.group = group;
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public ItemGroup getGroup()
    {
        return group;
    }
}
