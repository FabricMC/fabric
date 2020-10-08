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
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ItemApiProvider;

public final class ItemApiProviderImpl<A extends ApiProvider<A>> extends AbstractApiProvider<A> implements ItemApiProvider<A> {
	private final Reference2ReferenceOpenHashMap<Item, Function<ItemStack, A>> map = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	private ItemApiProviderImpl(Class<A> apiType, A absentProvider) {
		super(apiType, absentProvider);
		map.defaultReturnValue(e -> absentProvider);
	}

	@Override
	public void registerProviderForItem(Function<ItemStack, A> mapping, ItemConvertible... items) {
		Objects.requireNonNull(mapping, "encountered API provider mapping");

		for (final ItemConvertible item : items) {
			Objects.requireNonNull(item, "encountered null item in API provider mapping");

			if (map.putIfAbsent(item.asItem(), mapping) != null) {
				LOGGER.warn("Encountered duplicate API Provider registration for item " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Override
	public A getApiFromStack(ItemStack stack) {
		return map.get(stack.getItem()).apply(stack);
	}

	private static final ApiProviderRegistry<ItemApiProvider<?>> REGISTRY = new ApiProviderRegistry<>();

	public static <A extends ApiProvider<A>> ItemApiProvider<A> registerProvider(Identifier id, Class<A> apiType, A absentApi) {
		final ItemApiProviderImpl<A> result = new ItemApiProviderImpl<> (apiType, absentApi);
		REGISTRY.register(id, result);
		return result;
	}

	@Nullable
	public static ItemApiProvider<?> getProvider(Identifier id) {
		return REGISTRY.get(id);
	}
}
