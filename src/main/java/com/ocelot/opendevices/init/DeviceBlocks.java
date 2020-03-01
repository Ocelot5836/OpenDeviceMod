package com.ocelot.opendevices.init;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.block.ComponentBuilderBlock;
import com.ocelot.opendevices.block.DeviceBlock;
import com.ocelot.opendevices.block.LaptopBlock;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.render.LaptopTileEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.commons.lang3.Validate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class DeviceBlocks
{
    private static final Set<Block> BLOCKS = new HashSet<>();
    private static final Set<TileEntityType<?>> TILE_ENTITIES = new HashSet<>();

    public static final Block PLASTIC_BLOCK = register(new DeviceBlock("plastic_block", Block.Properties.create(Material.ROCK, MaterialColor.STONE)), new Item.Properties().group(OpenDevices.TAB));
    public static final Block COMPONENT_BUILDER = register(new ComponentBuilderBlock("component_builder"));

    public static final Block WHITE_LAPTOP = new LaptopBlock(DyeColor.WHITE);
    public static final Block ORANGE_LAPTOP = new LaptopBlock(DyeColor.ORANGE);
    public static final Block MAGENTA_LAPTOP = new LaptopBlock(DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_LAPTOP = new LaptopBlock(DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_LAPTOP = new LaptopBlock(DyeColor.YELLOW);
    public static final Block LIME_LAPTOP = new LaptopBlock(DyeColor.LIME);
    public static final Block PINK_LAPTOP = new LaptopBlock(DyeColor.PINK);
    public static final Block GRAY_LAPTOP = new LaptopBlock(DyeColor.GRAY);
    public static final Block LIGHT_GRAY_LAPTOP = new LaptopBlock(DyeColor.LIGHT_GRAY);
    public static final Block CYAN_LAPTOP = new LaptopBlock(DyeColor.CYAN);
    public static final Block PURPLE_LAPTOP = new LaptopBlock(DyeColor.PURPLE);
    public static final Block BLUE_LAPTOP = new LaptopBlock(DyeColor.BLUE);
    public static final Block BROWN_LAPTOP = new LaptopBlock(DyeColor.BROWN);
    public static final Block GREEN_LAPTOP = new LaptopBlock(DyeColor.GREEN);
    public static final Block RED_LAPTOP = new LaptopBlock(DyeColor.RED);
    public static final Block BLACK_LAPTOP = new LaptopBlock(DyeColor.BLACK);

    public static final TileEntityType<LaptopTileEntity> TE_LAPTOP = registerTileEntity("laptop", LaptopTileEntity::new, WHITE_LAPTOP, ORANGE_LAPTOP, MAGENTA_LAPTOP, LIGHT_BLUE_LAPTOP, YELLOW_LAPTOP, LIME_LAPTOP, PINK_LAPTOP, GRAY_LAPTOP, LIGHT_GRAY_LAPTOP, CYAN_LAPTOP, PURPLE_LAPTOP, BLUE_LAPTOP, BROWN_LAPTOP, GREEN_LAPTOP, RED_LAPTOP, BLACK_LAPTOP);

    @OnlyIn(Dist.CLIENT)
    public static void initClient()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(LaptopTileEntity.class, LaptopTileEntityRenderer.INSTANCE);
    }

    public static Block register(Block block, Item.Properties itemProperties)
    {
        return register(block, new BlockItem(block, itemProperties));
    }

    public static Block register(Block block, Item item)
    {
        register(block);
        DeviceItems.register(item.setRegistryName(Objects.requireNonNull(block.getRegistryName())));
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
        TILE_ENTITIES.add(type.setRegistryName(new ResourceLocation(OpenDevices.MOD_ID, name)));
        return type;
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
