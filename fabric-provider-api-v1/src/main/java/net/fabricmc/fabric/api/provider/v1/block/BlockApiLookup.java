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

package net.fabricmc.fabric.api.provider.v1.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An object that allows retrieving objects (called Apis) from blocks in a world.
 * Instances of this interface can be obtained through {@link BlockApiLookupRegistry#getLookup}.
 * <p>
 * When trying to {@link BlockApiLookup#get} an object, the block
 * or block entity at that position will be queried if it exists.
 * If it doesn't exist, or if it returns no object (i.e. {@code null}), the fallback block entity providers are queried in order.
 * Then, if no object is found that way, the generic fallback providers are queried.
 * </p>
 * <p>
 * If you are going to query objects a lot, consider using {@link BlockApiCache}, it may improve performance a lot.
 * </p>
 * @param <T> The type of the queried object
 * @param <C> The type of the additional context object
 */
public interface BlockApiLookup<T, C> {
	/**
	 * Retrieve an object from a block in the world. Consider using {@link BlockApiCache} if you are doing frequent queries at the same position.
	 * @param world The world
	 * @param pos The position of the block
	 * @param context Additional context for the query, defined by type parameter C
	 * @return The retrieved object, or {@code null} if no object was found
	 */
	@Nullable
	T get(World world, BlockPos pos, C context);

	/**
	 * Register a {@link BlockApiProvider} for some blocks.
	 * If you need to register a provider for a BlockEntity, please use {@link BlockApiLookup#registerForBlockEntities} instead.
	 * @param provider The provider
	 * @param blocks The blocks
	 * @throws NullPointerException If the provider or one of the blocks is null
	 */
	void registerForBlocks(BlockApiProvider<T, C> provider, Block... blocks);

	/**
	 * Register a {@link BlockEntityApiProvider} for some block entities.
	 * @param provider The provider
	 * @param blockEntityTypes The block entity types
	 * @throws NullPointerException If the provider or one of the block entity types is null
	 */
	void registerForBlockEntities(BlockEntityApiProvider<T, C> provider, BlockEntityType<?>... blockEntityTypes);

	/**
	 * Register a fallback provider for all block entities. It will be invoked if no object was found using the regular providers.
	 * This may have a big performance impact on all queries, use cautiously.
	 * @param fallbackProvider The fallback provider
	 * @throws NullPointerException If the provider is null
	 */
	void registerBlockEntityFallback(BlockEntityApiProvider<T, C> fallbackProvider);

	/**
	 * Register a fallback provider for all blocks. It will be invoked if no object was found using the regular providers or
	 * the fallback block entity providers. This may have a big performance impact on all queries, use cautiously.
	 * @param fallbackProvider The fallback provider
	 * @throws NullPointerException If the provider is null
	 */
	void registerBlockFallback(BlockApiProvider<T, C> fallbackProvider);

	@FunctionalInterface
	interface BlockApiProvider<T, C> {
		/**
		 * Return an object of type {@code T} if available in the world at the given pos with the given context, or {@link null} otherwise.
		 * @return An object of type {@code T} if available, or {@code null} otherwise.
		 */
		@Nullable
		T get(World world, BlockPos pos, C context);
	}

	@FunctionalInterface
	interface BlockEntityApiProvider<T, C> {
		/**
		 * Return an object of type {@code T} if available in the given block entity with the given context, or {@link null} otherwise.
		 * @return An object of type {@code T} if available, or {@code null} otherwise.
		 */
		@Nullable
		T get(BlockEntity blockEntity, C context);
	}
}
