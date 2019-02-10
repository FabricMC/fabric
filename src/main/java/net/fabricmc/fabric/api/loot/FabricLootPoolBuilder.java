/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.loot;

import net.fabricmc.fabric.impl.loot.LootPoolBuilderHooks;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;

public class FabricLootPoolBuilder extends LootPool.Builder {
	private final LootPoolBuilderHooks extended = (LootPoolBuilderHooks) this;

	private FabricLootPoolBuilder() {}

	private FabricLootPoolBuilder(LootPool pool) {
		FabricLootPool extendedPool = (FabricLootPool) pool;
		withRolls(extendedPool.getRolls());
		extended.fabric_getConditions().addAll(extendedPool.getConditions());
		extended.fabric_getEntries().addAll(extendedPool.getEntries());
		extended.fabric_getFunctions().addAll(extendedPool.getFunctions());
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
		super.withCondition(condition);
		return this;
	}

	@Override
	public FabricLootPoolBuilder withFunction(LootFunction.Builder function) {
		super.withFunction(function);
		return this;
	}

	public FabricLootPoolBuilder withEntry(LootEntry entry) {
		extended.fabric_getEntries().add(entry);
		return this;
	}

	public FabricLootPoolBuilder withCondition(LootCondition condition) {
		extended.fabric_getConditions().add(condition);
		return this;
	}

	public FabricLootPoolBuilder withFunction(LootFunction function) {
		extended.fabric_getFunctions().add(function);
		return this;
	}

	public static FabricLootPoolBuilder builder() {
		return new FabricLootPoolBuilder();
	}

	public static FabricLootPoolBuilder of(LootPool pool) {
		return new FabricLootPoolBuilder(pool);
	}
}
