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

import net.fabricmc.fabric.api.fluid.v1.ExtendedFabricFlowableFluid;
import net.fabricmc.fabric.test.fluid.block.MBlocks;
import net.fabricmc.fabric.test.fluid.item.MItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class RedFluid extends ExtendedFabricFlowableFluid {
	@Override
	public Fluid getFlowing() {
		return MFluids.RED_FLUID_FlOWING;
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
		return MBlocks.RED_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
	}

	@Override
	public int getFogColor(Entity entity) {
		return 0xff0000;
	}

	@Override
	public float getFogEnd(Entity entity) {
		return 10.0f;
	}

	@Override
	public float getFogStart(Entity entity) {
		return -10.0f;
	}

	@Override
	public double getViscosity(World world, Entity entity) {
		return 0.020d;
	}

	@Override
	public boolean canExtinguishFire() {
		return false;
	}

	@Override
	public boolean canLightFire() {
		return true;
	}

	@Override
	public boolean canPreventFallDamage() {
		return false;
	}

	@Override
	public void onSplash(World world, Entity entity) {
		entity.playSound(SoundEvents.ENTITY_STRIDER_STEP_LAVA, 1f, 1f);
		world.addParticle(ParticleTypes.GLOW, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
		world.addParticle(ParticleTypes.GLOW, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
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
