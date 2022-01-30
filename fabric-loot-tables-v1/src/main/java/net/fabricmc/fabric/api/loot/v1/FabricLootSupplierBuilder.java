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

package net.fabricmc.fabric.api.loot.v1;

import java.util.Collection;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;

/**
 * @deprecated Replaced with {@link FabricLootTableBuilder}.
 */
@Deprecated
public class FabricLootSupplierBuilder extends LootTable.Builder {
	protected FabricLootSupplierBuilder() { }

	private FabricLootSupplierBuilder(LootTable supplier) {
		copyFrom(supplier, true);
	}

	private FabricLootTableBuilder asV2() {
		return (FabricLootTableBuilder) this;
	}

	@Override
	public FabricLootSupplierBuilder pool(LootPool.Builder pool) {
		super.pool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder type(LootContextType type) {
		super.type(type);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder apply(LootFunction.Builder function) {
		super.apply(function);
		return this;
	}

	public FabricLootSupplierBuilder withPool(LootPool pool) {
		asV2().pool(pool);
		return this;
	}

	public FabricLootSupplierBuilder withFunction(LootFunction function) {
		asV2().apply(function);
		return this;
	}

	public FabricLootSupplierBuilder withPools(Collection<LootPool> pools) {
		asV2().pools(pools);
		return this;
	}

	public FabricLootSupplierBuilder withFunctions(Collection<LootFunction> functions) {
		asV2().apply(functions);
		return this;
	}

	/**
	 * Copies the pools and functions of the {@code supplier} to this builder.
	 * This is equal to {@code copyFrom(supplier, false)}.
	 */
	public FabricLootSupplierBuilder copyFrom(LootTable supplier) {
		return copyFrom(supplier, false);
	}

	/**
	 * Copies the pools and functions of the {@code supplier} to this builder.
	 * If {@code copyType} is true, the {@link FabricLootSupplier#getType type} of the supplier is also copied.
	 */
	public FabricLootSupplierBuilder copyFrom(LootTable supplier, boolean copyType) {
		FabricLootSupplier extendedSupplier = (FabricLootSupplier) supplier;
		asV2().pools(extendedSupplier.getPools());
		asV2().apply(extendedSupplier.getFunctions());

		if (copyType) {
			type(extendedSupplier.getType());
		}

		return this;
	}

	public static FabricLootSupplierBuilder builder() {
		return new FabricLootSupplierBuilder();
	}

	public static FabricLootSupplierBuilder of(LootTable supplier) {
		return new FabricLootSupplierBuilder(supplier);
	}
}
