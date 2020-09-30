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

/**
 * <p>All default blocks in the device mod.</p>
 *
 * @author Ocelot
 */
public class DeviceBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OpenDevices.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, OpenDevices.MOD_ID);

    public static final RegistryObject<LaptopBlock> WHITE_LAPTOP = register("white_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.WHITE)));
    public static final RegistryObject<LaptopBlock> ORANGE_LAPTOP = register("orange_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.ORANGE)));
    public static final RegistryObject<LaptopBlock> MAGENTA_LAPTOP = register("magenta_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.MAGENTA)));
    public static final RegistryObject<LaptopBlock> LIGHT_BLUE_LAPTOP = register("light_blue_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.LIGHT_BLUE)));
    public static final RegistryObject<LaptopBlock> YELLOW_LAPTOP = register("yellow_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.YELLOW)));
    public static final RegistryObject<LaptopBlock> LIME_LAPTOP = register("lime_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.LIME)));
    public static final RegistryObject<LaptopBlock> PINK_LAPTOP = register("pink_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.PINK)));
    public static final RegistryObject<LaptopBlock> GRAY_LAPTOP = register("gray_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.GRAY)));
    public static final RegistryObject<LaptopBlock> LIGHT_GRAY_LAPTOP = register("light_gray_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.LIGHT_GRAY)));
    public static final RegistryObject<LaptopBlock> CYAN_LAPTOP = register("cyan_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.CYAN)));
    public static final RegistryObject<LaptopBlock> PURPLE_LAPTOP = register("purple_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.PURPLE)));
    public static final RegistryObject<LaptopBlock> BLUE_LAPTOP = register("blue_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.BLUE)));
    public static final RegistryObject<LaptopBlock> BROWN_LAPTOP = register("brown_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.BROWN)));
    public static final RegistryObject<LaptopBlock> GREEN_LAPTOP = register("green_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.GREEN)));
    public static final RegistryObject<LaptopBlock> RED_LAPTOP = register("red_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.RED)));
    public static final RegistryObject<LaptopBlock> BLACK_LAPTOP = register("black_laptop", () -> new LaptopBlock(AbstractBlock.Properties.create(Material.ANVIL, DyeColor.BLACK)));

    public static final RegistryObject<TileEntityType<LaptopTileEntity>> LAPTOP_TILE_ENTITY = TILE_ENTITIES.register("laptop", () -> TileEntityType.Builder.create(LaptopTileEntity::new, WHITE_LAPTOP.get(), ORANGE_LAPTOP.get(), MAGENTA_LAPTOP.get(), LIGHT_BLUE_LAPTOP.get(), YELLOW_LAPTOP.get(), LIME_LAPTOP.get(), PINK_LAPTOP.get(), GRAY_LAPTOP.get(), LIGHT_GRAY_LAPTOP.get(), CYAN_LAPTOP.get(), PURPLE_LAPTOP.get(), BLUE_LAPTOP.get(), BROWN_LAPTOP.get(), GREEN_LAPTOP.get(), RED_LAPTOP.get(), BLACK_LAPTOP.get()).build(null));

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
