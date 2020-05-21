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

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;

/**
 * @deprecated Replaced with {@link net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder}.
 */
@Deprecated
public class FabricLootPoolBuilder extends net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder {
	private FabricLootPoolBuilder() { }

	private FabricLootPoolBuilder(LootPool pool) {
		copyFrom(pool, true);
	}

	@Override
	public FabricLootPoolBuilder rolls(LootTableRange range) {
		super.rolls(range);
		return this;
	}

	@Override
	public FabricLootPoolBuilder with(LootEntry.Builder<?> entry) {
		super.with(entry);
		return this;
	}

	@Override
	public FabricLootPoolBuilder conditionally(LootCondition.Builder condition) {
		super.conditionally(condition);
		return this;
	}

	@Override
	public FabricLootPoolBuilder apply(LootFunction.Builder function) {
		super.apply(function);
		return this;
	}

	public FabricLootPoolBuilder withEntry(LootEntry entry) {
		super.with(entry);
		return this;
	}

	public FabricLootPoolBuilder withCondition(LootCondition condition) {
		super.conditionally(condition);
		return this;
	}

	public FabricLootPoolBuilder withFunction(LootFunction function) {
		super.apply(function);
		return this;
	}

	/**
	 * Copies the entries, conditions and functions of the {@code pool} to this
	 * builder.
	 *
	 * <p>This is equal to {@code copyFrom(pool, false)}.
	 */
	public FabricLootPoolBuilder copyFrom(LootPool pool) {
		super.copyFrom(pool);
		return this;
	}

	/**
	 * Copies the entries, conditions and functions of the {@code pool} to this
	 * builder.
	 *
	 * <p>If {@code copyRolls} is true, the {@link FabricLootPool#getRolls rolls} of the pool are also copied.
	 */
	public FabricLootPoolBuilder copyFrom(LootPool pool, boolean copyRolls) {
		super.copyFrom(pool, copyRolls);
		return this;
	}

	public static FabricLootPoolBuilder builder() {
		return new FabricLootPoolBuilder();
	}

	public static FabricLootPoolBuilder of(LootPool pool) {
		return new FabricLootPoolBuilder(pool);
	}
}
