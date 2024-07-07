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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;

import net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder;

/**
 * Accesses loot pool fields for {@link FabricLootPoolBuilder#copyOf(LootPool)}.
 * These are normally available in the transitive access widener module.
 */
@Mixin(LootPool.class)
public interface LootPoolAccessor {
	@Accessor("rolls")
	LootNumberProvider fabric_getRolls();

	@Accessor("bonusRolls")
	LootNumberProvider fabric_getBonusRolls();

	@Accessor("entries")
	List<LootPoolEntry> fabric_getEntries();

	@Accessor("conditions")
	List<LootCondition> fabric_getConditions();

	@Accessor("functions")
	List<LootFunction> fabric_getFunctions();
}
