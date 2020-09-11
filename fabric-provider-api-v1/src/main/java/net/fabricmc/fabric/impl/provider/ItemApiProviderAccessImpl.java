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

package net.fabricmc.fabric.impl.provider;

import java.util.Objects;
import java.util.function.Function;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ItemApiProviderAccess;

public final class ItemApiProviderAccessImpl<P extends ApiProvider<P, A>, A> extends AbstractApiProviderAccess<P, A> implements ItemApiProviderAccess<P, A> {
	private final Reference2ReferenceOpenHashMap<Item, Function<ItemStack, P>> map = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	private ItemApiProviderAccessImpl(Class<A> apiType, P absentProvider) {
		super(apiType, absentProvider);
		map.defaultReturnValue(e -> absentProvider);
	}

	@Override
	public void registerProviderForItem(Function<ItemStack, P> mapping, ItemConvertible... items) {
		Objects.requireNonNull(mapping, "encountered API provider mapping");

		for (final ItemConvertible item : items) {
			Objects.requireNonNull(item, "encountered null item in API provider mapping");

			if (map.putIfAbsent(item.asItem(), mapping) != null) {
				LOGGER.warn("Encountered duplicate API Provider registration for item " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Override
	public P getProviderFromStack(ItemStack stack) {
		return map.get(stack.getItem()).apply(stack);
	}

	private static final ApiProviderAccessRegistry<ItemApiProviderAccess<?, ?>> REGISTRY = new ApiProviderAccessRegistry<>();

	public static <P extends ApiProvider<P, A>, A> ItemApiProviderAccess<P, A> registerAccess(Identifier id, Class<A> apiType, P absentProvider) {
		final ItemApiProviderAccessImpl<P, A> result = new ItemApiProviderAccessImpl<> (apiType, absentProvider);
		REGISTRY.register(id, result);
		return result;
	}

	public static ItemApiProviderAccess<?, ?> getAccess(Identifier id) {
		return REGISTRY.get(id);
	}
}
