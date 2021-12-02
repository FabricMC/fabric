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

package net.fabricmc.fabric.test.fluid.fluid;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.util.SoundParameters;
import net.fabricmc.fabric.test.fluid.block.MBlocks;
import net.fabricmc.fabric.test.fluid.item.MItems;

public abstract class RedFluid extends FabricFlowableFluid {
	@Override
	public Fluid getFlowing() {
		return MFluids.RED_FLUID_FLOWING;
	}

	@Override
	public Fluid getStill() {
		return MFluids.RED_FLUID;
	}

	@Override
	public Item getBucketItem() {
		return MItems.RED_FLUID_BUCKET;
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return 4;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 10;
	}

	@Override
	protected float getBlastResistance() {
		return 100.0f;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return MBlocks.RED_FLUID.getDefaultState().with(Properties.LEVEL_15, method_15741(state));
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL_LAVA);
	}

	@Override
	public int getFogColor(Entity entity, float tickDelta, ClientWorld world) {
		return 0xff0000;
	}

	@Override
	public float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 10.0f;
	}

	@Override
	public float getFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return -10.0f;
	}

	@Override
	public double getViscosity(World world, Entity entity) {
		return 0.020d;
	}

	@Override
	public int getEntityOnFireDuration(World world) {
		return 5;
	}

	@Override
	public float getHotDamage(World world) {
		return 2f;
	}

	@Override
	public SoundParameters getSplashSound(World world, Entity entity) {
		return SoundParameters.of(SoundEvents.ENTITY_STRIDER_STEP_LAVA);
	}

	@Override
	public void onSplash(@NotNull World world, @NotNull Entity entity) {
		world.addParticle(ParticleTypes.ASH, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
		world.addParticle(ParticleTypes.ASH, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
	}

	public static class Flowing extends RedFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}

	public static class Still extends RedFluid {
		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
