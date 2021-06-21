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

package net.fabricmc.fabric.api.loot.v2;

import java.util.Collection;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.mixin.loot.LootTableBuilderAccessor;

/**
 * An extended version of {@link LootTable.Builder}.
 */
public class FabricLootTableBuilder extends LootTable.Builder {
	// Any new LootPool.Builder methods have to be added to this class and copyOf().
	private final LootTableBuilderAccessor access = (LootTableBuilderAccessor) this;

	protected FabricLootTableBuilder() {
	}

	@Override
	public FabricLootTableBuilder pool(LootPool.Builder pool) {
		super.pool(pool);
		return this;
	}

	@Override
	public FabricLootTableBuilder type(LootContextType type) {
		super.type(type);
		return this;
	}

	@Override
	public FabricLootTableBuilder apply(LootFunction.Builder function) {
		super.apply(function);
		return this;
	}

	/**
	 * Adds a loot pool to this builder.
	 *
	 * @param pool the added pool
	 * @return this builder
	 */
	public FabricLootTableBuilder pool(LootPool pool) {
		access.getPools().add(pool);
		return this;
	}

	/**
	 * Applies a loot function to this builder.
	 *
	 * @param function the applied function
	 * @return this builder
	 */
	public FabricLootTableBuilder apply(LootFunction function) {
		access.getFunctions().add(function);
		return this;
	}

	/**
	 * Adds loot pools to this builder.
	 *
	 * @param pools the added pools
	 * @return this builder
	 */
	public FabricLootTableBuilder pools(Collection<? extends LootPool> pools) {
		access.getPools().addAll(pools);
		return this;
	}

	/**
	 * Applies loot functions to this builder.
	 *
	 * @param functions the applied functions
	 * @return this builder
	 */
	public FabricLootTableBuilder apply(Collection<? extends LootFunction> functions) {
		access.getFunctions().addAll(functions);
		return this;
	}

	/**
	 * Creates an empty builder.
	 *
	 * @return the created builder
	 */
	public static FabricLootTableBuilder create() {
		return new FabricLootTableBuilder();
	}

	/**
	 * Creates a builder copy of a loot table.
	 *
	 * @param table the loot table
	 * @return the copied builder
	 */
	public static FabricLootTableBuilder copyOf(LootTable table) {
		FabricLootTableBuilder builder = new FabricLootTableBuilder();

		builder.type(table.getType());
		builder.pools(FabricLootTables.getPools(table));
		builder.apply(FabricLootTables.getFunctions(table));

		return builder;
	}
}
