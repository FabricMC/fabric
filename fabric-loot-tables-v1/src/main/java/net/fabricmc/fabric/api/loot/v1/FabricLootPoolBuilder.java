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

import net.fabricmc.fabric.mixin.loot.LootPoolBuilderHooks;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;

public class FabricLootPoolBuilder extends LootPool.Builder {
	private final LootPoolBuilderHooks extended = (LootPoolBuilderHooks) this;

	private FabricLootPoolBuilder() {}

	private FabricLootPoolBuilder(LootPool pool) {
		copyFrom(pool, true);
	}

	@Override
	public FabricLootPoolBuilder withRolls(LootTableRange range) {
		super.withRolls(range);
		return this;
	}

	@Override
	public FabricLootPoolBuilder withEntry(LootEntry.Builder<?> entry) {
		super.withEntry(entry);
		return this;
	}

	@Override
	public FabricLootPoolBuilder withCondition(LootCondition.Builder condition) {
		super.method_356(condition);
		return this;
	}

	@Override
	public FabricLootPoolBuilder withFunction(LootFunction.Builder function) {
		super.method_353(function);
		return this;
	}

	public FabricLootPoolBuilder withEntry(LootEntry entry) {
		extended.getEntries().add(entry);
		return this;
	}

	public FabricLootPoolBuilder withCondition(LootCondition condition) {
		extended.getConditions().add(condition);
		return this;
	}

	public FabricLootPoolBuilder withFunction(LootFunction function) {
		extended.getFunctions().add(function);
		return this;
	}

	/**
	 * Copies the entries, conditions and functions of the {@code pool} to this
	 * builder.
	 *
	 * This is equal to {@code copyFrom(pool, false)}.
	 */
	public FabricLootPoolBuilder copyFrom(LootPool pool) {
		return copyFrom(pool, false);
	}

	/**
	 * Copies the entries, conditions and functions of the {@code pool} to this
	 * builder.
	 *
	 * If {@code copyRolls} is true, the {@link FabricLootPool#getRollsRange rolls} of the pool are also copied.
	 */
	public FabricLootPoolBuilder copyFrom(LootPool pool, boolean copyRolls) {
		FabricLootPool extendedPool = (FabricLootPool) pool;
		extended.getConditions().addAll(extendedPool.getConditions());
		extended.getFunctions().addAll(extendedPool.getFunctions());
		extended.getEntries().addAll(extendedPool.getEntries());

		if (copyRolls) {
			withRolls(extendedPool.getRollsRange());
		}

		return this;
	}

	public static FabricLootPoolBuilder builder() {
		return new FabricLootPoolBuilder();
	}

	public static FabricLootPoolBuilder of(LootPool pool) {
		return new FabricLootPoolBuilder(pool);
	}
}
