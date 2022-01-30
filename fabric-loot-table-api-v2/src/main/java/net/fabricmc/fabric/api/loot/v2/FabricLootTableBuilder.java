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

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;

/**
 * Convenience extensions to {@link LootTable.Builder}
 * for adding pre-built objects or collections.
 *
 * <p>This interface is automatically injected to {@link LootTable.Builder}.
 */
@ApiStatus.NonExtendable
public interface FabricLootTableBuilder {
	/**
	 * Adds a loot pool to this builder.
	 *
	 * @param pool the added pool
	 * @return this builder
	 */
	LootTable.Builder pool(LootPool pool);

	/**
	 * Applies a loot function to this builder.
	 *
	 * @param function the applied function
	 * @return this builder
	 */
	LootTable.Builder apply(LootFunction function);

	/**
	 * Adds loot pools to this builder.
	 *
	 * @param pools the added pools
	 * @return this builder
	 */
	LootTable.Builder pools(Collection<? extends LootPool> pools);

	/**
	 * Applies loot functions to this builder.
	 *
	 * @param functions the applied functions
	 * @return this builder
	 */
	LootTable.Builder apply(Collection<? extends LootFunction> functions);

	/**
	 * Creates a builder copy of a loot table.
	 *
	 * @param table the loot table
	 * @return the copied builder
	 */
	static LootTable.Builder copyOf(LootTable table) {
		LootTable.Builder builder = LootTable.builder();

		builder.type(table.getType());
		builder.pools(Arrays.asList(table.pools));
		builder.apply(Arrays.asList(table.functions));

		return builder;
	}
}
