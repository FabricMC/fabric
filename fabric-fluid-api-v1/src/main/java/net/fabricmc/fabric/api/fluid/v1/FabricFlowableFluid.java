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
import net.fabricmc.fabric.api.util.SoundParameters;
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
@SuppressWarnings("unused")
public abstract class FabricFlowableFluid extends FlowableFluid {
	/**
	 * Initializes a new FabricFlowableFluid.
	 */
	public FabricFlowableFluid() {}

	//todo reorganization
	//region FLUID PROPERTIES

	/**
	 * @return the sound played when filling a bucket with this fluid.
	 */
	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * @return the sound to play when the player enters the fluid.
	 */
	public SoundParameters getEnterSound(World world, Entity entity) {
		return SoundParameters.of(SoundEvents.AMBIENT_UNDERWATER_ENTER);
	}

	/**
	 * @param world The current world.
	 * @return the duration in seconds of fire when applied to entities.
	 */
	public int getEntityOnFireDuration(World world) {
		return 15;
	}

	/**
	 * @return the sound to play when the player exit from the fluid.
	 */
	public SoundParameters getExitSound(World world, Entity entity) {
		return SoundParameters.of(SoundEvents.AMBIENT_UNDERWATER_EXIT);
	}

	/**
	 * Get the fog color.
	 * @param entity The current entity that displays the fog.
	 * @param tickDelta The time passed from the last tick.
	 * @param world The current world.
	 */
	public int getFogColor(Entity entity, float tickDelta, ClientWorld world) {
		return -1;
	}

	/**
	 * Get the fog ending value.
	 * @param entity The current entity that displays the fog.
	 * @param fogType Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog Thick of fog.
	 */
	public float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return viewDistance;
	}

	/**
	 * Get the fog starting value.
	 * @param entity The current entity that displays the fog.
	 * @param fogType Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog Thick of fog.
	 */
	public float getFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 0f;
	}

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
	 * @return the sound to play when a boat navigates on the fluid.
	 */
	public Optional<SoundEvent> getPaddleSound() {
		return Optional.of(SoundEvents.ENTITY_BOAT_PADDLE_WATER);
	}

	/**
	 * @return the splash sound of the fluid.
	 */
	public SoundParameters getSplashSound(World world, Entity entity) {
		return SoundParameters.of(SoundEvents.ENTITY_GENERIC_SPLASH, 0.1f, 1f);
	}

	/**
	 * @return the swim sound of the fluid.
	 */
	public Optional<SoundEvent> getSwimSound() {
		return Optional.of(SoundEvents.ENTITY_GENERIC_SWIM);
	}

	/**
	 * @return the ambient sound to play when the player is submerged by the fluid.
	 */
	public SoundParameters getSubmergedAmbientSound(World world, Entity entity) {
		return SoundParameters.of(SoundEvents.AMBIENT_UNDERWATER_LOOP);
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
		return this.isIn(FabricFluidTags.FIRE_LIGHTER);
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
		if (!this.isIn(FabricFluidTags.FIRE_LIGHTER)) return;
		//If the fluid can light fire, its behaviour will be identical to the lava behaviour
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			int rnd = random.nextInt(3);
			if (rnd > 0) {
				BlockPos tPos = pos;
				for(int i = 0; i < rnd; ++i) {
					tPos = tPos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.canSetBlock(tPos)) return;

					BlockState blockState = world.getBlockState(tPos);
					if (blockState.isAir()) {
						Direction[] var3 = Direction.values();
						boolean canBurnBlock = false;

						for (Direction direction : var3) {
							if (this.hasBurnableBlock(world, pos.offset(direction))) {
								canBurnBlock = true;
							}
						}

						if (canBurnBlock) {
							world.setBlockState(tPos, AbstractFireBlock.getState(world, tPos));
							return;
						}
					} else if (blockState.getMaterial().blocksMovement()) return;
				}
			} else {
				for(int i = 0; i < 3; ++i) {
					BlockPos tPos = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.canSetBlock(tPos)) return;

					if (world.isAir(tPos.up()) && this.hasBurnableBlock(world, tPos)) {
						world.setBlockState(tPos.up(), AbstractFireBlock.getState(world, tPos));
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
			if (this.isIn(FabricFluidTags.FIRE_LIGHTER) && !this.isIn(FabricFluidTags.WET) && entityOnFireDuration > 0) {
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
