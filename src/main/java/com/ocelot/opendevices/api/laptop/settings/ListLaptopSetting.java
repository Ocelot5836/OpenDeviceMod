package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link ListNBT}. Can be used to read/write any list of NBT to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ListLaptopSetting extends ForgeRegistryEntry<LaptopSetting<?>> implements LaptopSetting<ListNBT>
{
    private int type;
    private ListNBT defaultValue;

    public ListLaptopSetting(int type, ListNBT defaultValue)
    {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public ListNBT read(CompoundNBT nbt)
    {
        return nbt.getList(String.valueOf(this.getRegistryName()), this.type);
    }

    @Override
    public void write(ListNBT value, CompoundNBT nbt)
    {
        nbt.put(String.valueOf(this.getRegistryName()), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(String.valueOf(this.getRegistryName()), Constants.NBT.TAG_LIST);
    }

    @Override
    public ListNBT getDefaultValue()
    {
        return defaultValue;
    }
}
