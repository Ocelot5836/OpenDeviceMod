package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link BlockPos}. Can be used to read/write any block pos to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class BlockPosLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<BlockPos>
{
    private BlockPos defaultValue;

    public BlockPosLaptopSetting(BlockPos defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public BlockPos read(CompoundNBT nbt)
    {
        return BlockPos.fromLong(nbt.getLong(String.valueOf(this.getRegistryName())));
    }

    @Override
    public void write(BlockPos value, CompoundNBT nbt)
    {
        nbt.putLong(String.valueOf(this.getRegistryName()), value.toLong());
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_LONG);
    }

    @Override
    public BlockPos getDefaultValue()
    {
        return defaultValue;
    }
}
