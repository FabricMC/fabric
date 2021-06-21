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

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.mixin.loot.LootTableAccessor;

/**
 * Utility methods related to loot tables.
 */
public final class FabricLootTables {
	private FabricLootTables() {
	}

	/**
	 * Gets an immutable list of a loot table's loot pools.
	 *
	 * @return the loot pools of the table
	 */
	public static List<LootPool> getPools(LootTable table) {
		Objects.requireNonNull(table, "table cannot be null");
		return ImmutableList.copyOf(((LootTableAccessor) table).getPools());
	}

	/**
	 * Gets an immutable list of a loot table's loot functions.
	 *
	 * @return the loot functions of the table
	 */
	public static List<LootFunction> getFunctions(LootTable table) {
		Objects.requireNonNull(table, "table cannot be null");
		return ImmutableList.copyOf(((LootTableAccessor) table).getFunctions());
	}
}
