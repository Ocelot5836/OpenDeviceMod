package com.ocelot.opendevices.api.computer.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for an array of the type {@link Integer}. Can be used to read/write any int array to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class IntArrayLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<int[]>
{
    private ResourceLocation registryName;
    private int[] defaultValue;

    public IntArrayLaptopSetting(ResourceLocation registryName, int... defaultValue)
    {
        this.registryName = registryName;
        this.defaultValue = defaultValue;
    }

    @Override
    public int[] read(CompoundNBT nbt)
    {
        return nbt.getIntArray(this.registryName.toString());
    }

    @Override
    public void write(int[] value, CompoundNBT nbt)
    {
        nbt.putIntArray(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_INT_ARRAY);
    }

    @Override
    public int[] getDefaultValue()
    {
        return defaultValue;
    }
}
