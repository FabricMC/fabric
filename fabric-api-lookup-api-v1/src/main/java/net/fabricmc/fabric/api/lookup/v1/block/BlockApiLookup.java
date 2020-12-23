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

package net.fabricmc.fabric.api.lookup.v1.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An object that allows retrieving Apis from blocks in a world.
 * Instances of this interface can be obtained through {@link BlockApiLookupRegistry#getLookup}.
 *
 * <p>When trying to {@link BlockApiLookup#get} an object, the block or block entity at that position will be queried if it exists.
 * If it doesn't exist, or if it returns {@code null}, the fallback providers are queried in order.
 *
 * <p>Note: If you are going to query Apis a lot, consider using {@link BlockApiCache}, it may drastically improve performance.
 *
 * <p><h3>Usage Example</h3>
 * Let us pretend we have the following interface that we would like to attach to some blocks depending on the direction.
 *
 * <pre>{@code
 * public interface FluidContainer {
 *     boolean containsFluids(); // return true if not empty
 * }}</pre>
 * Let us first create a static {@code BlockApiLookup} instance that will manage the registration and the query.
 *
 * <pre>{@code
 * public final class MyApi {
 *     public static final BlockApiLookup&lt;FluidContainer, Direction&gt; FLUID_CONTAINER = BlockApiLookupRegistry.getLookup(new Identifier("mymod:fluid_container"), FluidContainer.class, Direction.class);
 * }}</pre>
 * Using that, we can query instances of {@code FluidContainer}:
 *
 * <pre>{@code
 * FluidContainer container = MyApi.FLUID_CONTAINER.get(world, pos, direction);
 * if (container != null) {
 *     // Do something with the container
 *     if (container.containsFluids()) {
 *         System.out.println("It contains fluids!");
 *     }
 * }}</pre>
 * For the query to return a useful result, an Api must be registered.
 *
 * <pre>{@code
 * // With a block entity, the preferred way
 * MyApi.FLUID_CONTAINER.registerForBlockEntities((blockEntity, direction) -> {
 *     if (blockEntity instanceof YourBlockEntity) {
 *         // return your FluidContainer, ideally a field in the block entity, or null if there is none.
 *     }
 *     return null;
 * }, BLOCK_ENTITY_TYPE_1, BLOCK_ENTITY_TYPE_2);
 *
 * // Without a block entity
 * MyApi.FLUID_CONTAINER.registerForBlocks((world, pos, state, direction) -> {
 *     // return a FluidContainer for your block, or null if there is none
 * }, BLOCK_INSTANCE, ANOTHER_BLOCK_INSTANCE); // register as many blocks as you want
 *
 * // Block entity fallback, for example to interface with another mod's FluidInventory
 * MyApi.FLUID_CONTAINER.registerFallback((world, pos, state, blockEntity, direction) -> {
 *     if (blockEntity instanceof FluidInventory) {
 *         // return wrapper
 *     }
 *     return null;
 * });
 *
 * // General fallback, to interface with anything, for example another BlockApiLookup
 * MyApi.FLUID_CONTAINER.registerBlockFallback((world, pos, state, blockEntity, direction) -> {
 *     // return something if available, or null
 * });}</pre>
 *
 * <p><h3>Improving performance</h3>
 * When performing queries every tick, it is recommended to use {@link BlockApiCache BlockApiCache&lt;T, C&gt;}
 * instead of directly querying the {@code BlockApiLookup}.
 *
 * <pre>{@code
 * // 1) create and store an instance
 * BlockApiCache&lt;FluidContainer, Direction&gt; cache = BlockApiCache.create(MyApi.FLUID_CONTAINER, serverWorld, pos);
 *
 * // 2) use it later, the block entity instance will be cached among other things
 * FluidContainer container = cache.get(direction);
 * if (container != null) {
 *     // ...
 * }
 *
 * // 2bis) if the caller is able to cache the block state as well, for example by listening to neighbor updates,
 * /        that will further improve performance.
 * FluidContainer container = cache.get(direction, cachedBlockState);
 * if (container != null) {
 *     // ...
 * }
 *
 * // no need to destroy the cache, the garbage collector will take care of it}</pre>
 *
 * <p><h3>Generic context types</h3>
 * Note that {@code FluidContainer} and {@code Direction} were completely arbitrary in this example.
 * We can define any {@code BlockApiLookup&lt;T, C&gt;}, where {@code T} is the type of the queried Api, and {@code C} is the type of the additional context
 * (the direction parameter in the previous example).
 * If no context is necessary, {@code Void} should be used, and {@code null} instances should be passed.
 *
 * @param <T> The type of the queried object
 * @param <C> The type of the additional context object
 */
public interface BlockApiLookup<T, C> {
	/**
	 * Retrieve an Api from a block in the world. Consider using {@link BlockApiCache} if you are doing frequent queries at the same position.
	 *
	 * <p>Note: If the block state or the block entity is known, it is more efficient to use {@link BlockApiLookup#get(World, BlockPos, BlockState, BlockEntity, Object)}.
	 *
	 * @param world The world
	 * @param pos The position of the block
	 * @param context Additional context for the query, defined by type parameter C
	 * @return The retrieved Api, or {@code null} if no Api was found
	 */
	@Nullable
	default T get(World world, BlockPos pos, C context) {
		return get(world, pos, null, null, context);
	}

	/**
	 * Retrieve an Api from a block in the world. Consider using {@link BlockApiCache} if you are doing frequent queries at the same position.
	 *
	 * @param world The world
	 * @param pos The position of the block
	 * @param context Additional context for the query, defined by type parameter C
	 * @param state The block state at the target position, or null if unknown
	 * @param blockEntity The block entity at the target position if it is known, or null otherwise
	 * @return The retrieved Api, or {@code null} if no Api was found
	 */
	@Nullable
	T get(World world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context);

	/**
	 * Register a {@link BlockApiProvider} for some blocks.
	 * If you need to register a provider for a BlockEntity, please use {@link BlockApiLookup#registerForBlockEntities} instead.
	 *
	 * @param provider The provider
	 * @param blocks The blocks
	 * @throws NullPointerException If the provider or one of the blocks is null
	 */
	void registerForBlocks(BlockApiProvider<T, C> provider, Block... blocks);

	/**
	 * Register a {@link BlockEntityApiProvider} for some block entities.
	 *
	 * @param provider The provider
	 * @param blockEntityTypes The block entity types
	 * @throws NullPointerException If the provider or one of the block entity types is null
	 */
	void registerForBlockEntities(BlockEntityApiProvider<T, C> provider, BlockEntityType<?>... blockEntityTypes);

	/**
	 * Register a fallback provider for all blocks. It will be invoked if no object was found using the regular providers.
	 * This may have a big performance impact on all queries, use cautiously.
	 *
	 * @param fallbackProvider The fallback provider
	 * @throws NullPointerException If the provider is null
	 */
	void registerFallback(FallbackApiProvider<T, C> fallbackProvider);

	@FunctionalInterface
	interface BlockApiProvider<T, C> {
		/**
		 * Return an Api of type {@code T} if available in the world at the given pos with the given context, or {@code null} otherwise.
		 */
		@Nullable
		T get(World world, BlockPos pos, BlockState state, C context);
	}

	@FunctionalInterface
	interface BlockEntityApiProvider<T, C> {
		/**
		 * Return an Api of type {@code T} if available in the given block entity with the given context, or {@code null} otherwise.
		 */
		@Nullable
		T get(BlockEntity blockEntity, C context);
	}

	@FunctionalInterface
	interface FallbackApiProvider<T, C> {
		/**
		 * Return an Api of type {@code T} if available with the given context, or {@code null} otherwise.
		 * The block entity will be passed if available.
		 */
		@Nullable
		T get(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, C context);
	}
}
