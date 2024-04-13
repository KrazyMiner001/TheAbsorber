package krazyminer001.absorber.blocks.custom;

import com.mojang.serialization.MapCodec;
import krazyminer001.absorber.blocks.ModBlocks;
import krazyminer001.absorber.sounds.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AbsorberBlock extends Block {
    public static final MapCodec<AbsorberBlock> CODEC = createCodec(AbsorberBlock::new);
    public static final int ABSORB_RADIUS = 10;
    public static final int ABSORB_LIMIT = 128;

    public AbsorberBlock(Settings settings) {
        super(settings);
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    public MapCodec<AbsorberBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.update(world, pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    protected void update(World world, BlockPos pos) {
        if (this.absorbWater(world, pos)) {
            world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState(), 2);
            world.playSound(null, pos, ModSounds.BLOCK_ABSORBER_ABSORB, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }

    private boolean absorbWater(World world, BlockPos pos) {
        return BlockPos.iterateRecursively(pos, 10, 129, (currentPos, queuer) -> {
            Direction[] directions = DIRECTIONS;

            for (Direction direction : directions) {
                queuer.accept(currentPos.offset(direction));
            }

        }, (currentPos) -> {
            if (currentPos.equals(pos)) {
                return true;
            } else {
                BlockState blockState = world.getBlockState(currentPos);
                FluidState fluidState = world.getFluidState(currentPos);
                if (!fluidState.isIn(FluidTags.WATER)) {
                    return false;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof FluidDrainable fluidDrainable) {
                        if (!fluidDrainable.tryDrainFluid(null, world, currentPos, blockState).isEmpty()) {
                            return true;
                        }
                    }

                    if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                    } else {
                        if (!blockState.isOf(Blocks.KELP) && !blockState.isOf(Blocks.KELP_PLANT) && !blockState.isOf(Blocks.SEAGRASS) && !blockState.isOf(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                        dropStacks(blockState, world, currentPos, blockEntity);
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                    }

                    return true;
                }
            }
        }) > 1;
    }
}