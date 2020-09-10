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

import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.provider.ApiProviderAccessRegistry;
import net.fabricmc.fabric.impl.provider.ItemApiProviderAccessImpl;

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
public interface ItemApiProviderAccess<P extends ApiProvider<P, A>, A> extends ApiProviderAccess<P, A> {
	/**
	 * Causes the given items to provide component instances of this type
	 * by application of the given mapping function to item stacks having the given items.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping mapping function that derives a component instance from an access context
	 * @param items one or more items for which the function will apply
	 */
	void registerProviderForItem(Function<ItemStack, P> mapping, Item... items);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in the held in the main hand of the given player.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param player the player that may be holding an item containing a component of this type
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the held item
	 */
	default P getProviderFromHeldItem(ServerPlayerEntity player) {
		return getProviderFromStack(player.getMainHandStack());
	}

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in an item stack not held by a player.
	 *
	 * <p>The instance that is returned may be thread-local and should never be retained.
	 *
	 * @param stackGetter function that will be be used to retrieve item stack for component state
	 * @param stackSetter function that will be be used to persist item stack for component state
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the item stack
	 */
	P getProviderFromStack(ItemStack stack);

	static <P extends ApiProvider<P, A>, A> ItemApiProviderAccess<P, A> registerAcess(Identifier id, Class<A> apiType, P absentProvider) {
		return ItemApiProviderAccessImpl.registerAcess(id, apiType, absentProvider);
	}

	static ItemApiProviderAccess<?, ?> getAccess(Identifier id) {
		return ItemApiProviderAccessImpl.getAccess(id);
	}
}
