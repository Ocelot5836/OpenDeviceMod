package io.github.ocelot.opendevices.core.block;

import io.github.ocelot.opendevices.api.device.InteractableDevice;
import io.github.ocelot.opendevices.core.tileentity.LaptopTileEntity;
import io.github.ocelot.sonar.common.block.BaseBlock;
import io.github.ocelot.sonar.common.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class LaptopBlock extends BaseBlock implements IWaterLoggable
{
    public static final VoxelShape[] SHAPES = createShapes();

    public LaptopBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        boolean open = false;
        if (world.getTileEntity(pos) instanceof LaptopTileEntity)
        {
            open = ((LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(pos))).isOpen();
        }

        return SHAPES[state.get(HORIZONTAL_FACING).getHorizontalIndex() + (open ? 4 : 0)];
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.getTileEntity(pos) instanceof LaptopTileEntity)
        {
            if (!world.isRemote())
            {
                LaptopTileEntity te = (LaptopTileEntity) Objects.requireNonNull(world.getTileEntity(pos));
                if (player.isSecondaryUseActive())
                {
                    te.toggleOpen(player);
                }
                else if (te.isOpen())
                {
                    return InteractableDevice.attemptInteract(te, (ServerPlayerEntity) player) ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
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
        builder.add(HORIZONTAL_FACING, WATERLOGGED);
    }

    private static VoxelShape[] createShapes()
    {
        VoxelShape[] shapes = new VoxelShape[8];

        VoxelShapeHelper.Builder baseBuilder = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(1, 0, 3, 15, 2, 15));
        VoxelShapeHelper.Builder backBuilder = new VoxelShapeHelper.Builder(baseBuilder).append(Block.makeCuboidShape(1, 0, 1, 15, 12, 4));

        for (int i = 0; i < shapes.length; i++)
        {
            Direction direction = Direction.byHorizontalIndex(i % 4);
            shapes[i] = i >= 4 ? backBuilder.rotate(direction).build() : baseBuilder.rotate(direction).build();
        }
        return shapes;
    }
}
