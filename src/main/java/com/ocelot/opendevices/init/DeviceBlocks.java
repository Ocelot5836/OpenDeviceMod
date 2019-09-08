package com.ocelot.opendevices.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.apache.commons.lang3.Validate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class DeviceBlocks
{
    private static final Set<Block> BLOCKS = new HashSet<>();
    private static final Set<TileEntityType<?>> TILE_ENTITIES = new HashSet<>();

    // TODO add blocks

    public static Block register(Block block, Item.Properties itemProperties)
    {
        return register(block, createDefaultBlockItem(block, itemProperties));
    }

    public static Block register(Block block, Item item)
    {
        register(block);
        DeviceItems.register(item);
        return block;
    }

    public static Block register(Block block)
    {
        Validate.notNull(block.getRegistryName(), "Block %s does not have a registry name", block.getClass());
        BLOCKS.add(block);
        return block;
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(String name, Supplier<T> factory, Block... validBlocks)
    {
        TileEntityType<T> type = TileEntityType.Builder.create(factory, validBlocks).build(null);
        TILE_ENTITIES.add(type);
        return type;
    }

    public static Item createDefaultBlockItem(Block block, Item.Properties itemProperties)
    {
        return new BlockItem(block, itemProperties).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    public static Block[] getBlocks()
    {
        return BLOCKS.toArray(new Block[0]);
    }

    public static TileEntityType<?>[] getTileEntities()
    {
        return TILE_ENTITIES.toArray(new TileEntityType<?>[0]);
    }
}
