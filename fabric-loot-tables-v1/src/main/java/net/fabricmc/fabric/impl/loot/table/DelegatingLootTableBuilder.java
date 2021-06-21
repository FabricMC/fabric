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

package net.fabricmc.fabric.impl.loot.table;

import java.util.Collection;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.FabricLootTables;

/**
 * A {@link FabricLootSupplierBuilder} that delegates all methods to a v2 {@link FabricLootTableBuilder}.
 * Used for hooking the two {@code LootTableLoadingCallback} interfaces together.
 */
public class DelegatingLootTableBuilder extends FabricLootSupplierBuilder {
	private final FabricLootTableBuilder parent;

	public DelegatingLootTableBuilder(FabricLootTableBuilder parent) {
		this.parent = parent;
	}

	@Override
	public FabricLootSupplierBuilder pool(LootPool.Builder pool) {
		parent.pool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder type(LootContextType type) {
		parent.type(type);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder apply(LootFunction.Builder function) {
		parent.apply(function);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withPool(LootPool pool) {
		parent.pool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withFunction(LootFunction function) {
		parent.apply(function);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withPools(Collection<LootPool> pools) {
		parent.pools(pools);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withFunctions(Collection<LootFunction> functions) {
		parent.apply(functions);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder copyFrom(LootTable supplier, boolean copyType) {
		parent.pools(FabricLootTables.getPools(supplier));
		parent.apply(FabricLootTables.getFunctions(supplier));

		if (copyType) {
			parent.type(supplier.getType());
		}

		return this;
	}

	@Override
	public LootTable build() {
		return parent.build();
	}
}
