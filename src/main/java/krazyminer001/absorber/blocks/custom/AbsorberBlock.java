package krazyminer001.absorber.blocks.custom;

import com.mojang.serialization.MapCodec;
import krazyminer001.absorber.blocks.ModBlocks;
import krazyminer001.absorber.sounds.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
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

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem() == Items.WATER_BUCKET) {
            world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState(), 3);
            player.setStackInHand(hand, new ItemStack(Items.BUCKET));
            return ItemActionResult.CONSUME_PARTIAL;
        } else if (player.getStackInHand(hand).getItem() == Items.POTION) {
            PotionContentsComponent effects = player.getStackInHand(hand).get(DataComponentTypes.POTION_CONTENTS);
            if (effects == null) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (effects.potion().isPresent()) {
                if (effects.potion().get() == Potions.WATER) {
                    world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState().with(WetAbsorberBlock.LEVEL, 1), 3);
                    player.getStackInHand(hand).decrement(1);
                    player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
                    return ItemActionResult.CONSUME_PARTIAL;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}