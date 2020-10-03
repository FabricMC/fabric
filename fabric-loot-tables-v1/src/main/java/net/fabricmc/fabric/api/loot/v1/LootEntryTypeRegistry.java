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

import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;

import net.fabricmc.fabric.impl.loot.table.LootEntryTypeRegistryImpl;

/**
 * Fabric's extensions to {@code net.minecraft.loot.entry.LootEntries} for registering
 * custom loot entry types.
 *
 * @see #register
 */
public interface LootEntryTypeRegistry {
	LootEntryTypeRegistry INSTANCE = LootEntryTypeRegistryImpl.INSTANCE;

	/**
	 * Registers a loot entry type serializer by its ID.
	 *
	 * @param id the loot entry's ID
	 * @param serializer the loot entry serializer
	 */
	void register(Identifier id, JsonSerializer<? extends LootPoolEntry> serializer);
}
