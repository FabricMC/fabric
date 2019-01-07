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

import net.fabricmc.fabric.impl.loot.LootEntryRegistryImpl;
import net.minecraft.world.loot.entry.LootEntry;

/**
 * Fabric's extensions to {@code net.minecraft.world.loot.entry.LootEntries}.
 *
 * @see #register
 */
public interface LootEntryRegistry {
	LootEntryRegistry INSTANCE = LootEntryRegistryImpl.INSTANCE;

	/**
	 * Registers loot entry types.
	 *
	 * @param serializer the loot entry serializer
	 */
	void register(LootEntry.Serializer<?> serializer);
}
