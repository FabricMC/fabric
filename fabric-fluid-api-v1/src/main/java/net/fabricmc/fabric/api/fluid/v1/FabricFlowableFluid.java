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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

/**
 * Implements the basic behaviour of every fluid.
 */
@SuppressWarnings("unused")
public abstract class FabricFlowableFluid extends FlowableFluid {
	/**
	 * Initializes a new FabricFlowableFluid instance.
	 */
	public FabricFlowableFluid() {
	}

	/**
	 * Perform actions when fluid flows into a replaceable block.
	 *
	 * @param world The current world.
	 * @param pos   The position of the block broken.
	 * @param state The BlockState broken.
	 */
	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, @NotNull BlockState state) {
		final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	/**
	 * Get the sound played when filling a bucket with this fluid.
	 *
	 * @return Sound played when filling a bucket with this fluid.
	 */
	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * Get the color of the fluid fog.
	 *
	 * @param entity    The current entity that displays the fog.
	 * @param tickDelta The time passed from the last tick.
	 * @param world     The current world.
	 * @return Fog color (-1 = no fog).
	 */
	public int getFabricFogColor(Entity entity, float tickDelta, ClientWorld world) {
		return -1;
	}

	/**
	 * Get the distance after which the fog is fully opaque.
	 *
	 * @param entity       The current entity that displays the fog.
	 * @param fogType      Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog     Thick of fog.
	 * @return Fog ending value.
	 */
	public float getFabricFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 1f;
	}

	/**
	 * Get the distance after which the fog starts.
	 *
	 * @param entity       The current entity that displays the fog.
	 * @param fogType      Type of fog (can be SKY or TERRAIN).
	 * @param viewDistance The view distance of the current entity.
	 * @param thickFog     Thick of fog.
	 * @return Fog starting value.
	 */
	public float getFabricFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 0f;
	}

	/**
	 * Get the current fluid level.
	 *
	 * @param state The current FluidState.
	 * @return Current fluid level.
	 */
	@Override
	public int getLevel(FluidState state) {
		return isStill(state) ? FluidState.field_31728 : state.get(LEVEL);
	}

	/**
	 * Checks if this fluid is an instance of the specified fluid.
	 *
	 * @param fluid The current fluid.
	 * @return True if the given fluid is an instance of this fluid.
	 */
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == getStill() || fluid == getFlowing();
	}
}
