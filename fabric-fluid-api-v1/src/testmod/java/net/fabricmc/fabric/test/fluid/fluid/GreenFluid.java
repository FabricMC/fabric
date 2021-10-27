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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class GreenFluid extends ExtendedFabricFlowableFluid {
	@Override
	public Fluid getFlowing() {
		return MFluids.GREEN_FLUID_FlOWING;
	}

	@Override
	public Fluid getStill() {
		return MFluids.GREEN_FLUID;
	}

	@Override
	public Item getBucketItem() {
		return MItems.GREEN_FLUID_BUCKET;
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return 3;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		return 2;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 20;
	}

	@Override
	protected float getBlastResistance() {
		return 100.0f;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return MBlocks.GREEN_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
	}

	@Override
	public int getFogColor(Entity entity) {
		return 0x00ff00;
	}

	@Override
	public float getFogEnd(Entity entity) {
		return 2.0f;
	}

	@Override
	public float getFogStart(Entity entity) {
		return 0.25f;
	}

	@Override
	public double getViscosity(World world, Entity entity) {
		return 0.004d;
	}

	@Override
	public boolean canDrown() {
		return false;
	}

	@Override
	public void onTouching(World world, Entity entity) {
		super.onTouching(world, entity);
		if (!world.isClient) {
			if (entity instanceof PlayerEntity player && player.isCreative()) return;
			if (entity instanceof LivingEntity life) {
				StatusEffectInstance effect = life.getStatusEffect(StatusEffects.REGENERATION);
				if (effect != null) {
					if (effect.getDuration() < 80) life.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
				}
				else life.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
			}
		}
	}

	public static class Flowing extends GreenFluid {
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

	public static class Still extends GreenFluid {
		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
