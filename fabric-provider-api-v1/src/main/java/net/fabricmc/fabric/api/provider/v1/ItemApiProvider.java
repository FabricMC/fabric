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

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.provider.ItemApiProviderImpl;

/**
 * See {link ApiProviderAccess}. This subclass is for {@code ItemStack} game objects.
 */
public interface ItemApiProvider<A extends ApiProvider<A>> extends ApiProvider<A> {
	/**
	 * Causes the given items to to supply API provider instances by application of
	 * the given mapping function,
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a provider instance from an item stack
	 * @param items one or more types for which the mapping will apply
	 */
	void registerProviderForItem(Function<ItemStack, A> mapping, ItemConvertible... items);

	/**
	 * Retrieves an {@code ApiProvider} used to obtain an API instance if present.
	 *
	 * @return a {@code ApiProvider} used to obtain an API instance if present.
	 * Will be {@link #absentProvider()} if no API is present.
	 */
	A getApiFromStack(ItemStack stack);

	static <A extends ApiProvider<A>> ItemApiProvider<A> registerProvider(Identifier id, Class<A> apiType, A absentApi) {
		return ItemApiProviderImpl.registerProvider(id, apiType, absentApi);
	}

	@Nullable
	static ItemApiProvider<?> getProvider(Identifier id) {
		return ItemApiProviderImpl.getProvider(id);
	}
}
