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

package net.fabricmc.fabric.impl.loot.table;

import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class LootEntryTypeRegistryImpl implements net.fabricmc.fabric.api.loot.v1.LootEntryTypeRegistry {
	public LootEntryTypeRegistryImpl() { }

	@Override
	public void register(Identifier id, JsonSerializer<? extends LootPoolEntry> serializer) {
		Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, id, new LootPoolEntryType(serializer));
	}
}
