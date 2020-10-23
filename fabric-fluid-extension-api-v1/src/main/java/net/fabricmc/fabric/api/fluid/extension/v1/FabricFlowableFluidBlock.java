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

package net.fabricmc.fabric.api.fluid.extension.v1;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class FabricFlowableFluidBlock extends Block implements FluidDrainable {
	public static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

	protected final FabricFlowableFluid fluid;
	private final IntProperty stateIndexProperty;

	public FabricFlowableFluidBlock(Settings settings) {
		super(settings);
		this.fluid = getFluid();
		Objects.requireNonNull(fluid);
		stateIndexProperty = this.fluid.getStateIndexProperty();
	}

	@Override
	public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if (state.getFluidState().isStill()) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			return fluid;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return fluid.getDefaultState().with(stateIndexProperty, state.get(stateIndexProperty));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.getFluidState().getFluid().matchesType(fluid);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isAbove(COLLISION_SHAPE, pos, true) && state.getFluidState().isStill() && context.method_27866(world.getFluidState(pos.up()), fluid) ? COLLISION_SHAPE : VoxelShapes.empty();
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.getFluidState().hasRandomTicks();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		state.getFluidState().onRandomTick(world, pos, random);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(getFluid().getStateIndexProperty());
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		scheduleFluidTick(world, pos, state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.getFluidState().isStill() || newState.getFluidState().isStill()) {
			scheduleFluidTick(world, pos, state);
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		scheduleFluidTick(world, pos, state);
	}

	/**
	 * Called whenever this block tries to schedule a fluid tick.
	 */
	public void scheduleFluidTick(WorldAccess world, BlockPos pos, BlockState state) {
		world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), state.getFluidState().getFluid().getTickRate(world));
	}

	/**
	 * If a fluid is still, its state index is zero.
	 * If a fluid is flowing, its state index is non-zero and represents its level.
	 * @return The state index property used in BlockStates and FluidStates which correspond to this block.
	 */
	public IntProperty getStateIndexProperty() {
		return stateIndexProperty;
	}

	/**
	 * The returned value of this method should not change.
	 * @return A FabricFlowableFluid whose getFluidBlock method returns this block.
	 */
	public abstract FabricFlowableFluid getFluid();
}
