package io.github.ocelot.opendevices.core.init;

import io.github.ocelot.opendevices.OpenDevices;
import io.github.ocelot.opendevices.core.block.LaptopBlock;
import io.github.ocelot.opendevices.core.tileentity.LaptopTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeviceBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OpenDevices.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, OpenDevices.MOD_ID);

    public static final RegistryObject<LaptopBlock> RED_LAPTOP = register("red_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.RED)));

    public static final RegistryObject<TileEntityType<LaptopTileEntity>> LAPTOP_TILE_ENTITY = TILE_ENTITIES.register("laptop", () -> TileEntityType.Builder.create(LaptopTileEntity::new, RED_LAPTOP.get()).build(null));

    /**
     * Registers the specified block with a bound {@link BlockItem} under the specified id.
     *
     * @param name  The id of the block
     * @param block The block to register
     * @return The registry reference
     */
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block)
    {
        return register(name, block, new Item.Properties().group(OpenDevices.TAB));
    }

    /**
     * Registers the specified block with a bound {@link BlockItem} under the specified id.
     *
     * @param name           The id of the block
     * @param block          The block to register
     * @param itemProperties The properties of the block item to register
     * @return The registry reference
     */
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Item.Properties itemProperties)
    {
        return register(name, block, object -> new BlockItem(object.get(), itemProperties));
    }

    /**
     * Registers the specified block with a bound item under the specified id.
     *
     * @param name  The id of the block
     * @param block The block to register
     * @param item  The item to register or null for no item
     * @return The registry reference
     */
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Function<RegistryObject<T>, Item> item)
    {
        RegistryObject<T> object = BLOCKS.register(name, block);
        DeviceItems.ITEMS.register(name, () -> item.apply(object));
        return object;
    }
}
