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

import net.fabricmc.fabric.impl.provider.BlockApiProviderAccessImpl;

/**
 * See {link ApiProviderAccess}. This subclass is for {@code Block} game objects.
 *
 * <p>Block provider access is special because implementation can be more varied.
 * The same API interface could be implemented directly in a {@code BlockEntity},
 * as a member of a {@code BlockEntity} (in which case there could be more than one
 * API instance available), as a wrapper on top of {@code BlockState}, or a free-floating
 * instance associated with a block/block position via more exotic data structures.
 *
 * <p>Two access methods are provided - one for block entities and one for block state
 * within the world.  If a block entity instance is already acquired the block entity
 * access method should perform slightly better but both give equivalent results.
 */
public interface BlockApiProviderAccess<P extends ApiProvider<P, A>, A> extends ApiProviderAccess<P, A> {
	/**
	 * Causes the given blocks to supply API provider instances by application of
	 * the given mapping function. Use this version for blocks that
	 * may provide a component without the presence of a {@code BlockEntity}.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a provider instance from block state
	 * @param blocks one or more blocks for which the mapping will apply
	 */
	void registerProviderForBlock(BlockProviderFunction<P, A> mapping, Block... blocks);

	/**
	 * Causes the given blocks to to supply API provider instances by application of
	 * the given mapping function, Use this version for blocks where the {@code BlockEntity}
	 * houses or directly implements the component instance.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a provider instance from a block entity
	 * @param blockEntityTypes one or more types for which the mapping will apply
	 */
	void registerProviderForBlockEntity(BlockEntityProviderFunction<P, A> mapping, BlockEntityType<?>... blockEntityTypes);

	/**
	 * Retrieves an {@code ApiProvider} used to obtain an API instance if present.
	 *
	 * @param world the world where provider may be located
	 * @param pos the block position where provider may be located
	 * @param blockState the current block state at the given position within the world
	 * @return a {@code ApiProvider} used to obtain an API instance if present.
	 * Will be {@link #absentProvider()} if no API is present.
	 */
	P getProviderFromBlock(World world, BlockPos pos, BlockState blockState);

	/**
	 * Convenient version of {@link #getProviderFromBlock(World, BlockPos, BlockState)}
	 * to use when block state is not already retrieved.
	 */
	default P getProviderFromBlock(World world, BlockPos pos) {
		return getProviderFromBlock(world, pos, world.getBlockState(pos));
	}

	/**
	 * Retrieves an {@code ApiProvider} used to obtain an API instance if present.
	 *
	 * <p>If the API consumer somehow knows the block entity consistently implements the
	 * API or provider interface directly, casting the block entity instance will always be faster.
	 *
	 * @param blockEntity the block entity where the component may be located
	 * @return a {@code ApiProvider} used to obtain an API instance if present.
	 * Will be {@link #absentProvider()} if no API is present.
	 */
	P getProviderFromBlockEntity(BlockEntity blockEntity);

	/**
	 * @param <P> Identifies the API provider type
	 * @param <A> Identifies the API type
	 */
	@FunctionalInterface
	public interface BlockEntityProviderFunction<P extends ApiProvider<P, A>, A> {
		P getProvider(BlockEntity blockEntity);
	}

	/**
	 * @param <P> Identifies the API provider type
	 * @param <A> Identifies the API type
	 */
	@FunctionalInterface
	public interface BlockProviderFunction<P extends ApiProvider<P, A>, A> {
		P getProvider(World world, BlockPos pos, BlockState state);

		default P getProvider(World world, BlockPos pos) {
			return getProvider(world, pos, world.getBlockState(pos));
		}
	}

	/**
	 * Creates and returns new provider access instances.
	 *
	 * @param <P> Identifies the API provider type
	 * @param <A> Identifies the API type
	 * @param id Name-spaced identifier for this provider access
	 * @param apiType the class for instances of the provided API
	 * @param absentProvider immutable and non-allocating provider instance
	 * that always returns the API instance to be used when no instance is available.
	 * @return the created {@link ApiProviderAccess}
	 */
	static <P extends ApiProvider<P, A>, A> BlockApiProviderAccess<P, A> registerAccess(Identifier id, Class<A> apiType, P absentProvider) {
		return BlockApiProviderAccessImpl.registerAccess(id, apiType, absentProvider);
	}

	/**
	 * Retrieves a provider access instance registered earlier.
	 * May be unreliable during initialization due to undefined mod load order.
	 *
	 * @param id Name-spaced identifier for the requested provider access
	 * @return the requested {@link ApiProviderAccess}, or {@code null} if none is available
	 */
	/* @Nullable */
	static BlockApiProviderAccess<?, ?> getAccess(Identifier id) {
		return BlockApiProviderAccessImpl.getAccess(id);
	}
}
