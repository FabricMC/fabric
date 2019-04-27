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

package net.fabricmc.fabric.impl.loot;

import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;

import java.util.List;

public interface LootPoolBuilderHooks {
	List<LootEntry> fabric_getEntries();
	List<LootCondition> fabric_getConditions();
	List<LootFunction> fabric_getFunctions();
}
