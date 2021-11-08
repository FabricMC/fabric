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

import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
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
	 * Initializes a new FabricFlowableFluid.
	 */
	public FabricFlowableFluid() {}

	//region FLUID PROPERTIES

	/**
	 * @return the sound played when filling a bucket with this fluid.
	 */
	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * @param world The current world.
	 * @return the duration in seconds of fire when applied to entities.
	 */
	public int getEntityOnFireDuration(World world) {
		return 15;
	}

	/**
	 * Get the fog color.
	 * @param entity The current entity that displays the fog.
	 * @param tickDelta The time passed from the last tick.
	 * @param world The current world.
	 */
	public abstract int getFogColor(Entity entity, float tickDelta, ClientWorld world);

	/**
	 * Get the fog ending value.
	 * @param entity The current entity that displays the fog.
	 * @param fogType Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog Thick of fog.
	 */
	public abstract float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * Get the fog starting value.
	 * @param entity The current entity that displays the fog.
	 * @param fogType Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog Thick of fog.
	 */
	public abstract float getFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * @param world The current world.
	 * @return the hot damage to apply to entities.
	 */
	public float getHotDamage(World world) {
		return 0f;
	}

	/**
	 * @param state The current FluidState
	 * @return the maximum fluid level.
	 */
	public int getMaxLevel(FluidState state) {
		return 8;
	}

	/**
	 * @return the swim sound of the fluid.
	 */
	public Optional<SoundEvent> getSwimSound() {
		return Optional.of(SoundEvents.ENTITY_GENERIC_SWIM);
	}

	/**
	 * Get the fluid viscosity, that is equal to the pushing strength of the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	public double getViscosity(World world, Entity entity) {
		return 0.014d;
	}

	//endregion

	//region BEHAVIOUR PROPERTIES

	/**
	 * @return whether the given fluid can flow into this FluidState.
	 */
	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	/**
	 * @param state The current FluidState
	 * @return the current fluid level.
	 */
	@Override
	public int getLevel(FluidState state) {
		return isStill(state) ? getMaxLevel(state) : state.get(LEVEL);
	}

	/**
	 * @param fluid The current Fluid
	 * @return whether the given fluid is an instance of this fluid.
	 */
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == getStill() || fluid == getFlowing();
	}

	/**
	 * @return true if the fluids can execute randomly onRandomTick.
	 */
	@Override
	protected boolean hasRandomTicks() {
		return this.isIn(FabricFluidTags.FIRELIGHTER);
	}

	//endregion

	//region EVENTS

	/**
	 * Perform actions when fluid flows into a replaceable block.
	 */
	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, @NotNull BlockState state) {
		final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	/**
	 * Executed randomly every tick.
	 * @param world The current world.
	 * @param pos The current position.
	 * @param state The current FluidState.
	 * @param random Random generator.
	 */
	@Override
	public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
		if (!this.isIn(FabricFluidTags.FIRELIGHTER)) return;
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

	/**
	 * Event executed when an entity falls, or enters, into the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	public void onSplash(World world, Entity entity) {}

	/**
	 * Event executed when the entity is into the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	public void onSubmerged(@NotNull World world, Entity entity) {
		//Implements drowning living entities
		if (!world.isClient && entity instanceof LivingEntity life) {
			if (!this.isIn(FabricFluidTags.RESPIRABLE) && !life.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(life)) {
				if (!(life instanceof PlayerEntity player && player.getAbilities().invulnerable)) {
					life.setAir(life.getAir() - 1);
					if (life.getAir() <= -20) {
						life.setAir(0);
						life.damage(DamageSource.DROWN, 2f);
					}
				}
			}
		}
	}

	/**
	 * Event executed when the entity is touching the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	public void onTouching(@NotNull World world, Entity entity) {
		//Implements fire and hot damage on entities
		if (!world.isClient && !entity.isFireImmune()) {
			int entityOnFireDuration = getEntityOnFireDuration(world);
			float hotDamage = getHotDamage(world);
			if (this.isIn(FabricFluidTags.FIRELIGHTER) && !this.isIn(FabricFluidTags.WET) && entityOnFireDuration > 0) {
				entity.setOnFireFor(entityOnFireDuration);
			}
			if (hotDamage > 0 && entity.damage(DamageSource.IN_FIRE, hotDamage)) {
				entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + world.getRandom().nextFloat() * 0.4F);
			}
		}
	}

	//endregion

	/**
	 * Check if the block in the specified position is burnable.
	 * @param world The current world.
	 * @param pos The block position.
	 * @return true if the block in the specified position is burnable.
	 */
	@SuppressWarnings("deprecation")
	private boolean hasBurnableBlock(@NotNull WorldView world, @NotNull BlockPos pos) {
		return (pos.getY() < world.getBottomY() || pos.getY() >= world.getTopY() || world.isChunkLoaded(pos))
				&& world.getBlockState(pos).getMaterial().isBurnable();
	}
}
