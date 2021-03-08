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

package net.fabricmc.fabric.api.lookup.v1.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.impl.lookup.item.ItemApiLookupImpl;

/**
 * An object that allows retrieving APIs from blocks in a world.
 * Instances of this interface can be obtained through {@link #get}.
 *
 * <p>When trying to {@link #find} an API for an item, the provider registered for that item will be queried if it exists.
 * If it doesn't exist, or if it returns {@code null}, the fallback providers will be queried in order.
 *
 * <p><h3>Usage Example</h3>
 * Let us reuse {@code FluidContainer} from {@linkplain BlockApiLookup the BlockApiLookup example}.
 * We will query {@code FluidContainer} instances from (item, tag) pairs.
 *
 * <pre>{@code
 * public interface FluidContainer {
 *     boolean containsFluids(); // return true if not empty
 * }}</pre>
 * We need to create the ItemApiLookup, with context type {@code @Nullable CompoundTag}.
 *
 * <pre>{@code
 * public final class MyApi {
 *     public static final ItemApiLookup<FluidContainer, @Nullable CompoundTag> FLUID_CONTAINER_ITEM = ItemApiLookup.get(new Identifier("mymod:fluid_container"), FluidContainer.class, CompoundTag.class);
 * }}</pre>
 * API instances are easy to access:
 *
 * <pre>{@code
 * FluidContainer container = MyApi.FLUID_CONTAINER_ITEM.find(item, tag);
 * if (container != null) {
 *     // Do something with the container
 *     if (container.containsFluids()) {
 *         System.out.println("It contains fluids!");
 *     }
 * }}</pre>
 * For the query to return a useful result, we must expose the API:
 *
 * <pre>{@code
 * // If the item directly implements the interface, registerSelf can be used.
 * public class InfiniteWaterItem implements FluidContainer {
 *     ＠Override
 *     public boolean containsFluids() {
 *         return true; // This item always contains fluids!
 *     }
 * }
 * MyApi.FLUID_CONTAINER_ITEM.registerSelf(INFINITE_WATER_ITEM);
 *
 * // Otherwise, registerForItems can be used.
 * MyApi.FLUID_CONTAINER_ITEM.registerForItems((item, tag) -> {
 *     // return a FluidContainer for your item, or null if there is none
 * }, ITEM_INSTANCE, ANOTHER_ITEM_INSTANCE); // register as many items as you want
 *
 * // General fallback, to interface with anything, for example another ItemApiLookup.
 * MyApi.FLUID_CONTAINER_ITEM.registerFallback((item, tag) -> {
 *     // return something if available, or null
 * });}</pre>
 *
 * <p><h3>Generic context types</h3>
 * Note that {@code FluidContainer} and {@code @Nullable CompoundTag} were completely arbitrary in this example.
 * We can define any {@code ItemApiLookup&lt;A, C&gt;}, where {@code A} is the type of the queried API, and {@code C} is the type of the additional context
 * (the tag parameter in the previous example).
 * If no context is necessary, {@code Void} should be used, and {@code null} instances should be passed.
 *
 * @param <A> The type of the API.
 * @param <C> The type of the additional context object.
 */
@ApiStatus.NonExtendable
public interface ItemApiLookup<A, C> {
	/**
	 * Retrieve the {@link ItemApiLookup} associated with an identifier, or create it if it didn't exist yet.
	 *
	 * @param lookupId The unique identifier of the lookup.
	 * @param apiClass The class of the API.
	 * @param contextClass The class of the additional context.
	 * @return The unique lookup with the passed lookupId.
	 * @throws IllegalArgumentException If another {@code apiClass} or another {@code contextClass} was already registered with the same identifier.
	 */
	static <A, C> ItemApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return ItemApiLookupImpl.get(lookupId, apiClass, contextClass);
	}

	/**
	 * Attempt to retrieve an API from an item.
	 *
	 * @param item The item.
	 * @param context Additional context for the query, defined by type parameter C.
	 * @return The retrieved API, or {@code null} if no API was found.
	 */
	@Nullable
	A find(Item item, C context);

	/**
	 * Expose the API for the passed items directly implementing it.
	 *
	 * @param items Items for which to expose the API.
	 * @throws IllegalArgumentException If the API class is not assignable from a class of one of the items.
	 */
	void registerSelf(ItemConvertible... items);

	/**
	 * Expose the API for the passed items.
	 * The mapping from the parameters of the query to the API is handled by the passed {@link ItemApiProvider}.
	 *
	 * @param provider The provider.
	 * @param items The blocks.
	 */
	void registerForItems(ItemApiProvider<A, C> provider, ItemConvertible... items);

	/**
	 * Expose the API for all queries: the fallbacks providers will be invoked if no object was found using the regular providers.
	 *
	 * @param fallbackProvider The fallback provider.
	 */
	void registerFallback(ItemApiProvider<A, C> fallbackProvider);

	@FunctionalInterface
	interface ItemApiProvider<T, C> {
		/**
		 * Return an API of type {@code A} if available for the given item with the given context, or {@code null} otherwise.
		 *
		 * @param item The queried item.
		 * @param context Additional context passed to the query.
		 * @return An API of type {@code A}, or {@code null} if no API is available.
		 */
		@Nullable
		T find(Item item, C context);
	}
}
