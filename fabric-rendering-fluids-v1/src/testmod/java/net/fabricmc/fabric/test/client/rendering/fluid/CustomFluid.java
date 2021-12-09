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

package net.fabricmc.fabric.test.client.rendering.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;

public abstract class CustomFluid extends FabricFlowableFluid {
	public CustomFluid() {
	}

	@Override
	public Fluid getFlowing() {
		return TestFluids.CUSTOM_FLOWING;
	}

	@Override
	public Fluid getStill() {
		return TestFluids.CUSTOM;
	}

	@Override
	public Item getBucketItem() {
		return Items.WATER_BUCKET;
	}

	@Override
	protected boolean isInfinite() {
		return true;
	}

	@Override
	public int getFlowSpeed(WorldView world) {
		return 4;
	}

	@Override
	public int getFogColor(Entity entity, float tickDelta, ClientWorld world) {
		return -1;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return TestFluids.CUSTOM_BLOCK.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.matchesType(TestFluids.NO_OVERLAY);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends CustomFluid {
		public Flowing() {
		}

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

	public static class Still extends CustomFluid {
		public Still() {
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
