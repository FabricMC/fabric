/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.fluid.v1;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

/**
 * Implements the basic behaviour of every fluid.
 */
public abstract class FabricFlowableFluid extends FlowableFluid {
	/**
	 * Perform actions when fluid flows into a replaceable block.
	 */
	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	/**
	 * @return whether the given fluid can flow into this FluidState.
	 */
	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	/**
	 * @return true if the fluid can light fire.
	 */
	public boolean canLightFire() {
		return false;
	}

	/**
	 * @return the sound to play when a bucket item is filled with this fluid.
	 */
	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * @return the current fluid level.
	 */
	@Override
	public int getLevel(FluidState state) {
		return isStill(state) ? getMaxLevel(state) : state.get(LEVEL);
	}

	/**
	 * @return the maximum fluid level.
	 */
	public int getMaxLevel(FluidState state) {
		return 8;
	}

	/**
	 * @return whether the given fluid is an instance of this fluid.
	 */
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == getStill() || fluid == getFlowing();
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
		if (!canLightFire()) return;
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockPos = pos;
				for(int j = 0; j < i; ++j) {
					blockPos = blockPos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.canSetBlock(blockPos)) return;

					BlockState blockState = world.getBlockState(blockPos);
					if (blockState.isAir()) {
						Direction[] var3 = Direction.values();
						boolean canBurnBlock = false;

						for (Direction direction : var3) {
							if (this.hasBurnableBlock(world, pos.offset(direction))) {
								canBurnBlock = true;
							}
						}

						if (canBurnBlock) {
							world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
							return;
						}
					} else if (blockState.getMaterial().blocksMovement()) return;
				}
			} else {
				for(int k = 0; k < 3; ++k) {
					BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.canSetBlock(blockPos2)) return;

					if (world.isAir(blockPos2.up()) && this.hasBurnableBlock(world, blockPos2)) {
						world.setBlockState(blockPos2.up(), AbstractFireBlock.getState(world, blockPos2));
					}
				}
			}
		}
	}

	@Override
	protected boolean hasRandomTicks() {
		return canLightFire();
	}

	@SuppressWarnings("deprecation")
	private boolean hasBurnableBlock(@NotNull WorldView world, @NotNull BlockPos pos) {
		return (pos.getY() < world.getBottomY() || pos.getY() >= world.getTopY() || world.isChunkLoaded(pos))
				&& world.getBlockState(pos).getMaterial().isBurnable();
	}
}
