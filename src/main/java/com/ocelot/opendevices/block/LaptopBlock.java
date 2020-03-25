package com.ocelot.opendevices.block;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.api.util.ShapeHelper;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.task.UpdateLaptopUserTask;
import com.ocelot.opendevices.init.DeviceBlocks;
import com.ocelot.opendevices.init.DeviceMessages;
import com.ocelot.opendevices.item.DeviceBlockItem;
import com.ocelot.opendevices.network.MessageOpenGui;
import com.ocelot.opendevices.network.handler.MessageHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Objects;

public class LaptopBlock extends DeviceBlock implements IWaterLoggable
{
    public static final VoxelShape[] SHAPES = createShapes();
    public static final BooleanProperty SCREEN = BooleanProperty.create("screen");

    private DyeColor color;

    public LaptopBlock(DyeColor color)
    {
        super(color.getTranslationKey() + "_laptop", Block.Properties.create(Material.MISCELLANEOUS, color));
        this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(SCREEN, false).with(WATERLOGGED, false));
        DeviceBlocks.register(this, new DeviceBlockItem(this));
        this.color = color;
    }

    @Override
    protected void randomizeAddress(Device device, World world, BlockPos pos)
    {
        if (device instanceof LaptopTileEntity)
        {
            ((LaptopTileEntity) device).randomizeAddress();
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        boolean open = false;
        if (world.getTileEntity(pos) instanceof LaptopTileEntity)
        {
            open = ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).isOpen();
        }

        return getShape(state.get(HORIZONTAL_FACING), open);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.getTileEntity(pos) instanceof LaptopTileEntity)
        {
            if (!world.isRemote())
            {
                LaptopTileEntity te = (LaptopTileEntity) world.getTileEntity(pos);
                assert te != null;
                if (player.isSneaking())
                {
                    te.toggleOpen(player);
                }
                else if (te.view(player))
                {
                    if (player instanceof ServerPlayerEntity)
                    {
                        TaskManager.sendToTracking(new UpdateLaptopUserTask(te.getAddress(), player.getUniqueID()), world, pos);
                        DeviceMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MessageOpenGui(MessageHandler.GuiType.LAPTOP, pos));
                    }
                }
                else if (te.isOpen())
                {
                    PlayerEntity userPlayer = te.getUser();
                    if (userPlayer != null)
                    {
                        player.sendStatusMessage(new TranslationTextComponent("block." + OpenDevices.MOD_ID + ".laptop.using.specific", userPlayer.getDisplayName().getFormattedText()), true);
                    }
                    else
                    {
                        player.sendStatusMessage(new TranslationTextComponent("block." + OpenDevices.MOD_ID + ".laptop.using"), true);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new LaptopTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, SCREEN, WATERLOGGED);
    }

    public DyeColor getColor()
    {
        return color;
    }

    public static VoxelShape getShape(Direction direction, boolean open)
    {
        return SHAPES[direction.getHorizontalIndex() + (open ? 4 : 0)];
    }

    private static VoxelShape[] createShapes()
    {
        VoxelShape[] shapes = new VoxelShape[8];

        for (int i = 0; i < shapes.length; i++)
        {
            int index = i % 4;
            boolean open = i >= 4;
            Direction direction = Direction.byHorizontalIndex(index);

            if (open)
            {
                shapes[i] = VoxelShapes.combineAndSimplify(ShapeHelper.makeCuboidShape(1, 0, 1, 15, 2, 15, direction), ShapeHelper.makeCuboidShape(1, 2, 1, 15, 13, 4, direction), IBooleanFunction.OR);
            }
            else
            {
                shapes[i] = ShapeHelper.makeCuboidShape(1, 0, 3, 15, 2, 15, direction);
            }
        }
        return shapes;
    }
}
