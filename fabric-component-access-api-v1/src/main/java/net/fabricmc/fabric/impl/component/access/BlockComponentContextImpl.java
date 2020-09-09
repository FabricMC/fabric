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

package net.fabricmc.fabric.impl.component.access;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.component.access.v1.BlockComponentContext;

@SuppressWarnings("rawtypes")
public final class BlockComponentContextImpl extends AbstractComponentContextImpl implements BlockComponentContext {
	private Block block;
	private BlockPos pos;
	private BlockState blockState;
	private BlockEntity blockEntity;

	@Override
	public Block block() {
		return block;
	}

	@Override
	public BlockPos pos() {
		BlockPos result = pos;

		if (result == null && blockEntity != null) {
			result = blockEntity.getPos();
			pos = result;
		}

		return result;
	}

	@Override
	public BlockEntity blockEntity() {
		BlockEntity result = blockEntity;

		if (result == null && world != null) {
			result = world.getBlockEntity(pos);
			blockEntity = result;
		}

		return result;
	}

	@Override
	public BlockState blockState() {
		return blockState;
	}

	@Override
	protected World getWorldLazily() {
		return blockEntity == null ? null : blockEntity.getWorld();
	}

	@SuppressWarnings("unchecked")
	private BlockComponentContextImpl prepare(ComponentTypeImpl componentType, World world, BlockPos pos) {
		this.componentType = componentType;
		this.world = world;
		this.pos = pos;
		blockEntity = null;
		blockState = world.getBlockState(pos);
		block = blockState.getBlock();
		mapping = componentType.getMapping(block);
		return this;
	}

	@SuppressWarnings("unchecked")
	private BlockComponentContextImpl prepare(ComponentTypeImpl componentType, World world, BlockPos pos, BlockState blockState) {
		this.componentType = componentType;
		this.world = world;
		this.pos = pos;
		blockEntity = null;
		this.blockState = blockState;
		block = blockState.getBlock();
		mapping = componentType.getMapping(block);
		return this;
	}

	@SuppressWarnings("unchecked")
	private BlockComponentContextImpl prepare(ComponentTypeImpl componentType, BlockEntity blockEntity) {
		this.componentType = componentType;
		world = null;
		pos = null;
		this.blockEntity = blockEntity;
		blockState = blockEntity.getCachedState();
		block = blockState.getBlock();
		mapping = componentType.getMapping(block);
		return this;
	}

	private static final ThreadLocal<BlockComponentContextImpl> POOL = ThreadLocal.withInitial(BlockComponentContextImpl::new);

	static BlockComponentContextImpl get(ComponentTypeImpl componentType, World world, BlockPos pos) {
		return POOL.get().prepare(componentType, world, pos);
	}

	static BlockComponentContextImpl get(ComponentTypeImpl componentType, World world, BlockPos pos, BlockState blockState) {
		return POOL.get().prepare(componentType, world, pos, blockState);
	}

	static BlockComponentContextImpl get(ComponentTypeImpl componentType, BlockEntity blockEntity) {
		return POOL.get().prepare(componentType, blockEntity);
	}
}
