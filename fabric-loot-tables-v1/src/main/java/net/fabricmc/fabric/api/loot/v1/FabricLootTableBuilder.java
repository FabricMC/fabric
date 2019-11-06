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

import net.fabricmc.fabric.mixin.loot.LootTableBuilderHooks;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

public class FabricLootTableBuilder extends LootTable.Builder {
	private final LootTableBuilderHooks extended = (LootTableBuilderHooks) this;

	protected FabricLootTableBuilder() { }

	private FabricLootTableBuilder(LootTable table) {
		copyFrom(table, true);
	}

	@Override
	public FabricLootTableBuilder withPool(LootPool.Builder pool) {
		super.withPool(pool);
		return this;
	}

	@Override
	public FabricLootTableBuilder withType(LootContextType type) {
		super.withType(type);
		return this;
	}

	@Override
	public FabricLootTableBuilder withFunction(LootFunction.Builder function) {
		super.method_335(function);
		return this;
	}

	public FabricLootTableBuilder withPool(LootPool pool) {
		extended.getPools().add(pool);
		return this;
	}

	public FabricLootTableBuilder withFunction(LootFunction function) {
		extended.getFunctions().add(function);
		return this;
	}

	public FabricLootTableBuilder withPools(Collection<LootPool> pools) {
		pools.forEach(this::withPool);
		return this;
	}

	public FabricLootTableBuilder withFunctions(Collection<LootFunction> functions) {
		functions.forEach(this::withFunction);
		return this;
	}

	/**
	 * Copies the pools and functions of the {@code supplier} to this builder.
	 * This is equal to {@code copyFrom(supplier, false)}.
	 */
	public FabricLootTableBuilder copyFrom(LootTable table) {
		return copyFrom(table, false);
	}

	/**
	 * Copies the pools and functions of the {@code supplier} to this builder.
	 * If {@code copyType} is true, the {@link FabricLootTable#getType type} of the supplier is also copied.
	 */
	public FabricLootTableBuilder copyFrom(LootTable table, boolean copyType) {
		FabricLootTable extendedSupplier = (FabricLootTable) table;
		extended.getPools().addAll(extendedSupplier.getPools());
		extended.getFunctions().addAll(extendedSupplier.getFunctions());

		if (copyType) {
			withType(extendedSupplier.getType());
		}

		return this;
	}

	public static FabricLootTableBuilder builder() {
		return new FabricLootTableBuilder();
	}

	public static FabricLootTableBuilder of(LootTable table) {
		return new FabricLootTableBuilder(table);
	}
}
