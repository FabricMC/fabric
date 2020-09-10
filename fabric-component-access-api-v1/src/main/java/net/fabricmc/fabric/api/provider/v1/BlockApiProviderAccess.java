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

package net.fabricmc.fabric.api.provider.v1;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.provider.ApiProviderAccessRegistry;
import net.fabricmc.fabric.impl.provider.BlockApiProviderAccessImpl;

/**
 * Describes and provides access to component instances that may be retrieved
 * for blocks, items or entities.
 *
 * <p>This interface should never be implemented by mod authors. Create new instances
 * using {@link ApiProviderAccessRegistry#createAccess(net.minecraft.util.Identifier, Class, ApiProvider)}.
 *
 * @param <P> Identifies the API provider type
 * @param <A> Identifies the API type
 */
public interface BlockApiProviderAccess<P extends ApiProvider<P, A>, A> extends ApiProviderAccess<P, A> {
	/**
	 * Causes the given blocks to provide component instances of this type
	 * by application of the given mapping function. Use this version for blocks that
	 * may provide a component without the presence of a {@code BlockEntity}.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a component instance from an access context
	 * @param blocks one or more blocks for which the function will apply
	 */
	void registerProviderForBlock(BlockProviderFunction<P, A> mapping, Block... blocks);

	/**
	 * Causes the given blocks to provide component instances of this type
	 * via block entities associated with the given blocks.
	 *
	 * <p>Use this version for blocks where the {@code BlockEntity} houses or
	 * directly implements the component instance.
	 *
	 * @param blocks one or more blocks that will provide components in this way
	 */
	void registerProviderForBlockEntity(BlockEntityProviderFunction<P, A> mapping, BlockEntityType<?>... blockEntityTypes);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * <p>Note that {@link #getProviderFromBlock(World, BlockPos, BlockState)} may be more performant
	 * if 1) you know this component type requires block state and 2) the block state
	 * and the given position is already on the call stack.
	 *
	 * @param world the server world where the component may be located
	 * @param pos the block position where the component may be located
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location
	 */
	default P getProviderFromBlock(World world, BlockPos pos) {
		return getProviderFromBlock(world, pos, world.getBlockState(pos));
	}

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param world the server world where the component may be located
	 * @param pos the block position where the component may be located
	 * @param blockState the current block state at the given position within the world
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location
	 */
	P getProviderFromBlock(World world, BlockPos pos, BlockState blockState);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present at the given location.
	 *
	 * <p>If the API consumer somehow knows the block entity implements the
	 * provider interface directly, casting the BE instance will always be
	 * faster. This is useful when that is unknown to the consumer, or when
	 * the BlockEntity exposes the target API as a member.
	 *
	 * @param blockEntity the block entity where the component may be located
	 * @return a {@code ComponentAccess} to access components of this type
	 */
	P getProviderFromBlockEntity(BlockEntity blockEntity);

	/**
	 * @param <P> Identifies the API provider type
	 * @param <A> Identifies the API type
	 */
	@FunctionalInterface
	public interface BlockEntityProviderFunction <P extends ApiProvider<P, A>, A> {
		P getProvider(BlockEntity blockEntity);
	}

	/**
	 * @param <P> Identifies the API provider type
	 * @param <A> Identifies the API type
	 */
	@FunctionalInterface
	public interface BlockProviderFunction <P extends ApiProvider<P, A>, A> {
		P getProvider(World world, BlockPos pos, BlockState state);

		default P getProvider(World world, BlockPos pos) {
			return getProvider(world, pos, world.getBlockState(pos));
		}
	}

	static <P extends ApiProvider<P, A>, A> BlockApiProviderAccess<P, A> registerAcess(Identifier id, Class<A> apiType, P absentProvider) {
		return BlockApiProviderAccessImpl.registerAcess(id, apiType, absentProvider);
	}

	static BlockApiProviderAccess<?, ?> getAccess(Identifier id) {
		return BlockApiProviderAccessImpl.getAccess(id);
	}
}
