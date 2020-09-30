package io.github.ocelot.opendevices.core.device.serializer;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.api.device.TileEntityDevice;
import io.github.ocelot.opendevices.api.device.serializer.DeviceSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class TileEntityDeviceSerializer extends ForgeRegistryEntry<DeviceSerializer<?>> implements DeviceSerializer<TileEntityDevice>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public TileEntityDeviceSerializer()
    {
        this.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, "tile_entity"));
    }

    @Nullable
    private static TileEntityDevice get(World world, RegistryKey<World> dimension, BlockPos pos)
    {
        World deviceWorld = world.isRemote() ? world : ((ServerWorld) world).getServer().getWorld(dimension);
        if (deviceWorld == null)
            return null;

        TileEntity te = deviceWorld.getTileEntity(pos);
        if (!(te instanceof TileEntityDevice))
        {
            LOGGER.warn("Tile Entity at " + pos + " is not an instanceof DeviceTileEntity.");
            return null;
        }

        return (TileEntityDevice) te;
    }

    @Override
    public void serialize(World world, TileEntityDevice device, CompoundNBT nbt)
    {
        World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, device.getDeviceDimensionKey()).resultOrPartial(LOGGER::error).ifPresent(dimNbt -> nbt.put("Dimension", dimNbt));
        BlockPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, device.getDevicePos()).resultOrPartial(LOGGER::error).ifPresent(posNbt -> nbt.put("Pos", posNbt));
    }

    @Nullable
    @Override
    public TileEntityDevice deserialize(World world, CompoundNBT nbt)
    {
        RegistryKey<World> dimension = World.CODEC.parse(NBTDynamicOps.INSTANCE, nbt.get("Dimension")).result().orElseThrow(() -> new IllegalArgumentException("Invalid Device Dimension: " + nbt.get("Dimension")));
        BlockPos pos = BlockPos.CODEC.parse(NBTDynamicOps.INSTANCE, nbt.get("Pos")).resultOrPartial(LOGGER::error).orElseThrow(() -> new IllegalArgumentException("Invalid Device Position: " + nbt.get("Pos")));
        return get(world, dimension, pos);
    }
}
