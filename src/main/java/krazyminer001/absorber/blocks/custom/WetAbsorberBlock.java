package krazyminer001.absorber.blocks.custom;

import com.mojang.serialization.MapCodec;
import krazyminer001.absorber.blocks.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class WetAbsorberBlock extends Block {
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    public WetAbsorberBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 3));
    }

    public static final MapCodec<WetAbsorberBlock> CODEC = createCodec(WetAbsorberBlock::new);

    public MapCodec<WetAbsorberBlock> getCodec() {
        return CODEC;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {

        if (world.getDimension().ultrawarm()) {
            world.setBlockState(pos, ModBlocks.ABSORBER.getDefaultState(), 3);
            world.syncWorldEvent(2009, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }

    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (player.getStackInHand(hand).getItem() == Items.GLASS_BOTTLE) {
            switch (state.get(LEVEL)) {
                case 1 -> {
                    player.getStackInHand(hand).decrement(1);
                    world.setBlockState(pos, ModBlocks.ABSORBER.getDefaultState(), 3);
                    player.giveItemStack(Items.POTION.getDefaultStack());
                    return ItemActionResult.CONSUME_PARTIAL;
                }
                case 2 -> {
                    player.getStackInHand(hand).decrement(1);
                    world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState().with(LEVEL, 1), 3);
                    player.giveItemStack(Items.POTION.getDefaultStack());
                    return ItemActionResult.CONSUME_PARTIAL;
                }
                case 3 -> {
                    player.getStackInHand(hand).decrement(1);
                    world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState().with(LEVEL, 2), 3);
                    player.giveItemStack(Items.POTION.getDefaultStack());
                    return ItemActionResult.CONSUME_PARTIAL;
                }
            }
        }
        if (player.getStackInHand(hand).getItem() == Items.BUCKET) {
            player.getStackInHand(hand).decrement(1);
            world.setBlockState(pos, ModBlocks.ABSORBER.getDefaultState(), 3);
            player.giveItemStack(Items.WATER_BUCKET.getDefaultStack());
            return ItemActionResult.CONSUME_PARTIAL;
        }
        if (player.getStackInHand(hand).getItem() == Items.POTION) {
            if (world.getBlockState(pos).get(LEVEL) == 3) {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            PotionContentsComponent effects = player.getStackInHand(hand).get(DataComponentTypes.POTION_CONTENTS);
            if (effects == null) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (effects.potion().isPresent()) {
                if (effects.potion().get() == Potions.WATER) {
                    player.getStackInHand(hand).decrement(1);
                    player.giveItemStack(Items.GLASS_BOTTLE.getDefaultStack());
                    world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState().with(LEVEL, world.getBlockState(pos).get(LEVEL) + 1), 3);
                    return ItemActionResult.CONSUME_PARTIAL;
                } else {
                    return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random(random);
        if (direction != Direction.UP) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                double d = pos.getX();
                double e = pos.getY();
                double f = pos.getZ();
                if (direction == Direction.DOWN) {
                    e -= 0.05;
                    d += random.nextDouble();
                    f += random.nextDouble();
                } else {
                    e += random.nextDouble() * 0.8;
                    if (direction.getAxis() == Direction.Axis.X) {
                        f += random.nextDouble();
                        if (direction == Direction.EAST) {
                            ++d;
                        } else {
                            d += 0.05;
                        }
                    } else {
                        d += random.nextDouble();
                        if (direction == Direction.SOUTH) {
                            ++f;
                        } else {
                            f += 0.05;
                        }
                    }
                }

                world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            if (world.getBlockState(pos.down()).getBlock() == Blocks.CAULDRON) {
                world.setBlockState(pos.down(), Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, world.getBlockState(pos).get(LEVEL)), 3);
                world.setBlockState(pos, ModBlocks.ABSORBER.getDefaultState(), 3);
                world.syncWorldEvent(2009, pos.down(), 0);
                world.playSound(null, pos.down(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
            }
            if (world.getBlockState(pos.down()).getBlock() == Blocks.WATER_CAULDRON) {
                int waterInCauldron = world.getBlockState(pos.down()).get(LeveledCauldronBlock.LEVEL);
                int waterInAbsorber = world.getBlockState(pos).get(LEVEL);
                int newWaterInCauldron = waterInCauldron + waterInAbsorber;
                int newWaterInAbsorber = 0;
                if (newWaterInCauldron > 2) {
                    newWaterInAbsorber = newWaterInCauldron - 3;
                    newWaterInCauldron = 3;
                }
                world.setBlockState(pos.down(), Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, newWaterInCauldron), 3);
                world.setBlockState(pos, ModBlocks.FILLED_ABSORBER.getDefaultState().with(LEVEL, newWaterInAbsorber), 3);
                world.syncWorldEvent(2009, pos.down(), 0);
                world.playSound(null, pos.down(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
            }
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
}
