package com.ocelot.opendevices.core.registry;

import com.ocelot.opendevices.api.device.TileEntityDevice;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityDeviceSerializer extends ForgeRegistryEntry<DeviceSerializer<?>> implements DeviceSerializer<TileEntityDevice>
{
    @Nullable
    @Override
    public TileEntityDevice read(World world, UUID address, CompoundNBT nbt)
    {
        if (!nbt.contains("pos", Constants.NBT.TAG_LONG) || !nbt.contains("dimension", Constants.NBT.TAG_INT))
            return null;
        if (world.getDimension().getType().getId() != nbt.getInt("dimension"))
            return null;
        TileEntity te = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
        return te instanceof TileEntityDevice ? (TileEntityDevice) te : null;
    }

    @Override
    public CompoundNBT write(ServerWorld world, TileEntityDevice device)
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("pos", device.getPos().toLong());
        nbt.putInt("dimension", world.getDimension().getType().getId());
        return nbt;
    }

    @Override
    public boolean canRead(World world, UUID address, CompoundNBT nbt)
    {
        if (!nbt.contains("pos", Constants.NBT.TAG_LONG) || !nbt.contains("dimension", Constants.NBT.TAG_INT))
            return false;
        if (world.getDimension().getType().getId() != nbt.getInt("dimension"))
            return false;
        return world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos"))) instanceof TileEntityDevice;
    }
}
