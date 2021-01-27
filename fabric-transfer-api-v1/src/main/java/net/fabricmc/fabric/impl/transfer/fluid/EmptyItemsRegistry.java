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

package net.fabricmc.fabric.impl.transfer.fluid;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidApi;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class EmptyItemsRegistry {
	private static final Map<Item, EmptyItemProvider> PROVIDERS = new IdentityHashMap<>();

	public static synchronized void registerEmptyItem(Item emptyItem, Fluid fluid, long amount, Function<ItemKey, ItemKey> keyMapping) {
		PROVIDERS.computeIfAbsent(emptyItem, item -> {
			EmptyItemProvider provider = new EmptyItemProvider();
			FluidApi.ITEM.register(provider, emptyItem);
			return provider;
		});
		EmptyItemProvider provider = PROVIDERS.get(emptyItem);

		// We use a copy-on-write strategy to register the fluid filling if possible
		Map<Fluid, FillInfo> copy = new IdentityHashMap<>(provider.acceptedFluids);
		copy.putIfAbsent(fluid, new FillInfo(amount, keyMapping));
		provider.acceptedFluids = copy;
	}

	private static class EmptyItemProvider implements ItemApiLookup.ItemApiProvider<Storage<Fluid>, ContainerItemContext> {
		private volatile Map<Fluid, FillInfo> acceptedFluids = new IdentityHashMap<>();

		@Override
		public @Nullable Storage<Fluid> get(ItemKey itemKey, ContainerItemContext context) {
			return new EmptyItemStorage(itemKey, context);
		}

		private class EmptyItemStorage implements InsertionOnlyStorage<Fluid> {
			private final ItemKey initialKey;
			private final ContainerItemContext ctx;

			private EmptyItemStorage(ItemKey initialKey, ContainerItemContext ctx) {
				this.initialKey = initialKey;
				this.ctx = ctx;
			}

			@Override
			public long insert(Fluid fluid, long maxAmount, Transaction transaction) {
				FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);

				if (ctx.getCount(transaction) == 0) return 0;
				FillInfo fillInfo = acceptedFluids.get(fluid);
				if (fillInfo == null) return 0;

				if (maxAmount >= fillInfo.amount) {
					if (ctx.transform(1, fillInfo.keyMapping.apply(initialKey), transaction)) {
						return fillInfo.amount;
					}
				}

				return 0;
			}

			@Override
			public boolean forEach(Visitor<Fluid> visitor, Transaction transaction) {
				return false;
			}
		}
	}

	private static class FillInfo {
		private final long amount;
		private final Function<ItemKey, ItemKey> keyMapping;

		private FillInfo(long amount, Function<ItemKey, ItemKey> keyMapping) {
			this.amount = amount;
			this.keyMapping = keyMapping;
		}
	}
}
