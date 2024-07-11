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

package net.fabricmc.fabric.mixin.loot;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder;

/**
 * The implementation of the injected interface {@link FabricLootPoolBuilder}.
 * Simply implements the new methods by adding the relevant objects inside the lists.
 */
@Mixin(LootPool.Builder.class)
abstract class LootPoolBuilderMixin implements FabricLootPoolBuilder {
	@Shadow
	@Final
	private ImmutableList.Builder<LootPoolEntry> entries;

	@Shadow
	@Final
	private ImmutableList.Builder<LootCondition> conditions;

	@Shadow
	@Final
	private ImmutableList.Builder<LootFunction> functions;

	@Unique
	private LootPool.Builder self() {
		// noinspection ConstantConditions
		return (LootPool.Builder) (Object) this;
	}

	@Override
	public LootPool.Builder with(LootPoolEntry entry) {
		this.entries.add(entry);
		return self();
	}

	@Override
	public LootPool.Builder with(Collection<? extends LootPoolEntry> entries) {
		this.entries.addAll(entries);
		return self();
	}

	@Override
	public LootPool.Builder conditionally(LootCondition condition) {
		this.conditions.add(condition);
		return self();
	}

	@Override
	public LootPool.Builder conditionally(Collection<? extends LootCondition> conditions) {
		this.conditions.addAll(conditions);
		return self();
	}

	@Override
	public LootPool.Builder apply(LootFunction function) {
		this.functions.add(function);
		return self();
	}

	@Override
	public LootPool.Builder apply(Collection<? extends LootFunction> functions) {
		this.functions.addAll(functions);
		return self();
	}
}
