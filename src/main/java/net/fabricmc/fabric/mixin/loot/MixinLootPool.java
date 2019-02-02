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

package net.fabricmc.fabric.mixin.loot;

import net.fabricmc.fabric.api.loot.FabricLootPool;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface MixinLootPool extends FabricLootPool {
	@Accessor
	@Override
	LootEntry[] getEntries();

	@Accessor
	@Override
	LootCondition[] getConditions();

	@Accessor
	@Override
	LootFunction[] getFunctions();

	@Accessor
	@Override
	LootTableRange getRolls();

	@Accessor
	@Override
	UniformLootTableRange getBonusRolls();

	@Accessor
	@Override
	void setEntries(LootEntry[] entries);

	@Accessor
	@Override
	void setConditions(LootCondition[] conditions);

	@Accessor
	@Override
	void setFunctions(LootFunction[] entries);

	@Accessor
	@Override
	void setRolls(LootTableRange rolls);

	@Accessor
	@Override
	void setBonusRolls(UniformLootTableRange bonusRolls);
}
