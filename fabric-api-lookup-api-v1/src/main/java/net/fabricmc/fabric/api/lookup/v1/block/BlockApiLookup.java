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
 * <p>
 * When trying to {@link BlockApiLookup#get} an object, the block
 * or block entity at that position will be queried if it exists.
 * If it doesn't exist, or if it returns {@code null}, the fallback block entity providers are queried in order.
 * Then, if no object is found that way, the generic fallback providers are queried.
 * </p>
 * <p>
 * Note: If you are going to query Apis a lot, consider using {@link BlockApiCache}, it may drastically improve performance.
 * </p>
 * <p><h3>Usage Example</h3>
 * Let us pretend we have the following interface that we would like to attach to some blocks depending on the direction.
 * <pre>
 * public interface FluidContainer {
 *     boolean containsFluids(); // return true if not empty
 * }</pre>
 * Let us first create a static {@code BlockApiLookup} instance that will manage the registration and the query.
 * <pre>
 * public class Whatever {
 *     public static final BlockApiLookup&lt;FluidContainer, Direction&gt; FLUID_CONTAINER =
 *         BlockApiLookupRegistry.getLookup(new Identifier("mymod:fluid_container"), FluidContainer.class, Direction.class);
 * }</pre>
 * Using that, we can query instances of {@code FluidContainer}:
 * <pre>
 * FluidContainer container = Whatever.FLUID_CONTAINER.get(world, pos, direction);
 * if (container != null) {
 *     // Do something with the container
 *     if (container.containsFluids()) {
 *         System.out.println("It contains fluids!");
 *     }
 * }</pre>
 * For the query to return a useful result, an Api must be registered.
 * <pre>
 * // With a block entity, the preferred way
 * Whatever.FLUID_CONTAINER.registerForBlockEntities((blockEntity, direction) -> {
 *     if (blockEntity instanceof YourBlockEntity) {
 *         // return your FluidContainer, ideally a field in the block entity, or null if there is none.
 *     }
 *     return null;
 * }, BLOCK_ENTITY_TYPE_1, BLOCK_ENTITY_TYPE_2);
 *
 * // Without a block entity
 * Whatever.FLUID_CONTAINER.registerForBlocks((world, pos, blockState, direction) -> {
 *     // return a FluidContainer for your block, or null if there is none
 * }, BLOCK_INSTANCE, ANOTHER_BLOCK_INSTANCE); // register as many blocks as you want
 *
 * // Block entity fallback, for example to interface with another mod's FluidInventory
 * Whatever.FLUID_CONTAINER.registerBlockEntityFallback((blockEntity, direction) -> {
 *     if (blockEntity instanceof FluidInventory) {
 *         // return wrapper
 *     }
 *     return null;
 * });
 *
 * // General fallback, to interface with anything, for example another BlockApiLookup
 * Whatever.FLUID_CONTAINER.registerBlockFallback((world,pos,blockState,direction)->{
 *     // return something if available, or null
 * });</pre>
 * </p>
 *
 * <p><h3>Improving performance</h3>
 * When performing queries every tick, it is recommended to use {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache BlockApiCache&lt;T, C&gt;}
 * instead of directly querying the {@code BlockApiLookup}.
 * <pre>
 * // 1) create and store an instance
 * BlockApiCache&lt;FluidContainer, Direction&gt; cache = BlockApiCache.create(Whatever.FLUID_CONTAINER, serverWorld, pos);
 *
 * // 2) use it later, the block entity instance will be cached among other things
 * FluidContainer container = cache.get(direction);
 * if (container != null) {
 *     // ...
 * }
 *
 * // no need to destroy the cache, the garbage collector will take care of it</pre>
 * </p>
 * @param <T> The type of the queried object
 * @param <C> The type of the additional context object
 */
public interface BlockApiLookup<T, C> {
	/**
	 * Retrieve an Api from a block in the world. Consider using {@link BlockApiCache} if you are doing frequent queries at the same position.
	 * @param world The world
	 * @param pos The position of the block
	 * @param context Additional context for the query, defined by type parameter C
	 * @return The retrieved Api, or {@code null} if no Api was found
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
}
