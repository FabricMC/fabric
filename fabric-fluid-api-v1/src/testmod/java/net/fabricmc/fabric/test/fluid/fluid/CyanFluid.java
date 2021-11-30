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

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.test.fluid.block.MBlocks;
import net.fabricmc.fabric.test.fluid.item.MItems;

public abstract class CyanFluid extends FabricFlowableFluid {
	@Override
	public Fluid getFlowing() {
		return MFluids.CYAN_FLUID_FLOWING;
	}

	@Override
	public Fluid getStill() {
		return MFluids.CYAN_FLUID;
	}

	@Override
	public Item getBucketItem() {
		return MItems.CYAN_FLUID_BUCKET;
	}

	@Override
	protected boolean isInfinite() {
		return true;
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return 2;
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
		return MBlocks.CYAN_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
	}

	@Override
	public int getMaxLevel(FluidState state) {
		return 6;
	}

	@Override
	public int getFogColor(Entity entity, float tickDelta, ClientWorld world) {
		return 0x00ffff;
	}

	@Override
	public float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 1.0f;
	}

	@Override
	public float getFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		return 0.25f;
	}

	@Override
	public double getViscosity(World world, Entity entity) {
		return 0.014d;
	}

	public static class Flowing extends CyanFluid {
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

	public static class Still extends CyanFluid {
		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
