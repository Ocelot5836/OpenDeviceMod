package com.ocelot.opendevices.api.laptop.settings;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * <p>Represents a {@link LaptopSetting} for the type {@link ListNBT}. Can be used to read/write any list of NBT to/from the system settings.</p>
 *
 * @author Ocelot
 * @see LaptopSetting
 */
public class ListLaptopSetting implements LaptopSetting<ListNBT>
{
    private ResourceLocation registryName;
    private int type;
    private ListNBT defaultValue;

    public ListLaptopSetting(ResourceLocation registryName, int type, ListNBT defaultValue)
    {
        this.registryName = registryName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public ListNBT read(CompoundNBT nbt)
    {
        return nbt.getList(this.registryName.toString(), this.type);
    }

    @Override
    public void write(ListNBT value, CompoundNBT nbt)
    {
        nbt.put(this.registryName.toString(), value);
    }

    @Override
    public boolean contains(CompoundNBT nbt)
    {
        return nbt.contains(this.registryName.toString(), Constants.NBT.TAG_LIST);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    @Override
    public ListNBT getDefaultValue()
    {
        return defaultValue;
    }
}
