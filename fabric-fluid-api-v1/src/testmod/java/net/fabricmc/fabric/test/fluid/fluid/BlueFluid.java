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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public abstract class BlueFluid extends ExtendedFabricFlowableFluid {
	@Override
	public Fluid getFlowing() {
		return MFluids.BLUE_FLUID_FlOWING;
	}

	@Override
	public Fluid getStill() {
		return MFluids.BLUE_FLUID;
	}

	@Override
	public Item getBucketItem() {
		return MItems.BLUE_FLUID_BUCKET;
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return 2;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		return 3;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 40;
	}

	@Override
	protected float getBlastResistance() {
		return 100.0f;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return MBlocks.BLUE_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
	}

	@Override
	public int getFogColor(Entity entity) {
		return 0x0000ff;
	}

	@Override
	public float getFogEnd(Entity entity) {
		return 1.0f;
	}

	@Override
	public float getFogStart(Entity entity) {
		return 0.25f;
	}

	@Override
	public double getViscosity(World world, Entity entity) {
		return 0.014d;
	}

	@Override
	public void onSplash(World world, Entity entity) {
		entity.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1f, 1f);
		world.addParticle(ParticleTypes.SPLASH, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
		world.addParticle(ParticleTypes.SPLASH, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
		world.addParticle(ParticleTypes.SPLASH, entity.getX(), entity.getY(), entity.getZ(), 0.02d, 0.02d, 0.02d);
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
		if (!state.isStill() && !(Boolean)state.get(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			world.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	public static class Flowing extends BlueFluid {
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

	public static class Still extends BlueFluid {
		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
