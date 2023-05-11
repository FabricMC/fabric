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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;

/**
 * Accesses loot table fields for {@link net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder#copyOf(LootTable)}.
 * These are normally available in the transitive access widener module.
 */
@Mixin(LootTable.class)
public interface LootTableAccessor {
	@Accessor("pools")
	LootPool[] fabric_getPools();

	@Accessor("functions")
	LootFunction[] fabric_getFunctions();

	@Accessor("field_44892")
	Identifier fabric_getRandomSequenceId();
}
