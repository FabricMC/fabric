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
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.mixin.loot.LootPoolBuilderAccessor;

/**
 * An extended version of {@link LootPool.Builder}.
 */
public class FabricLootPoolBuilder extends LootPool.Builder {
	// Any new LootPool.Builder methods have to be added to this class and copyOf().
	private final LootPoolBuilderAccessor access = (LootPoolBuilderAccessor) this;

	protected FabricLootPoolBuilder() {
	}

	// Vanilla overrides

	@Override
	public FabricLootPoolBuilder rolls(LootTableRange range) {
		super.rolls(range);
		return this;
	}

	@Override
	public FabricLootPoolBuilder with(LootPoolEntry.Builder<?> entry) {
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

	// Custom methods

	/**
	 * Sets the bonus rolls of this builder.
	 *
	 * @param bonusRolls the bonus rolls range
	 * @return this builder
	 * @see FabricLootPools#getBonusRolls(LootPool)
	 */
	public FabricLootPoolBuilder bonusRolls(UniformLootTableRange bonusRolls) {
		access.setBonusRollsRange(bonusRolls);
		return this;
	}

	/**
	 * Adds an entry to this builder.
	 *
	 * @param entry the added loot entry
	 * @return this builder
	 */
	public FabricLootPoolBuilder with(LootPoolEntry entry) {
		access.getEntries().add(entry);
		return this;
	}

	/**
	 * Adds entries to this builder.
	 *
	 * @param entries the added loot entries
	 * @return this builder
	 */
	public FabricLootPoolBuilder with(Collection<? extends LootPoolEntry> entries) {
		access.getEntries().addAll(entries);
		return this;
	}

	/**
	 * Adds a condition to this builder.
	 *
	 * @param condition the added condition
	 * @return this builder
	 */
	public FabricLootPoolBuilder conditionally(LootCondition condition) {
		access.getConditions().add(condition);
		return this;
	}

	/**
	 * Adds conditions to this builder.
	 *
	 * @param conditions the added conditions
	 * @return this builder
	 */
	public FabricLootPoolBuilder conditionally(Collection<? extends LootCondition> conditions) {
		access.getConditions().addAll(conditions);
		return this;
	}

	/**
	 * Applies a function to this builder.
	 *
	 * @param function the applied loot function
	 * @return this builder
	 */
	public FabricLootPoolBuilder apply(LootFunction function) {
		access.getFunctions().add(function);
		return this;
	}

	/**
	 * Applies loot functions to this builder.
	 *
	 * @param functions the applied loot functions
	 * @return this builder
	 */
	public FabricLootPoolBuilder apply(Collection<? extends LootFunction> functions) {
		access.getFunctions().addAll(functions);
		return this;
	}

	/**
	 * Creates an empty loot pool builder.
	 *
	 * @return the created builder
	 */
	public static FabricLootPoolBuilder create() {
		return new FabricLootPoolBuilder();
	}

	/**
	 * Creates a builder copy of a loot pool.
	 *
	 * @param pool the loot pool
	 * @return the copied builder
	 */
	public static FabricLootPoolBuilder copyOf(LootPool pool) {
		FabricLootPoolBuilder builder = new FabricLootPoolBuilder();

		builder.rolls(FabricLootPools.getRolls(pool));
		builder.bonusRolls(FabricLootPools.getBonusRolls(pool));
		builder.with(FabricLootPools.getEntries(pool));
		builder.conditionally(FabricLootPools.getConditions(pool));
		builder.apply(FabricLootPools.getFunctions(pool));

		return builder;
	}
}
