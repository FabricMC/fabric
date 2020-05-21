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

import net.minecraft.loot.entry.LootEntry;

/**
 * Fabric's extensions to {@code net.minecraft.loot.entry.LootEntries} for registering
 * custom loot entry types.
 *
 * @see #register
 * @deprecated Replaced with {@link net.fabricmc.fabric.api.loot.v2.LootEntryTypeRegistry}.
 */
public interface LootEntryTypeRegistry {
	LootEntryTypeRegistry INSTANCE = new LootEntryTypeRegistry() {
		@Override
		public void register(LootEntry.Serializer<?> serializer) {
			net.fabricmc.fabric.api.loot.v2.LootEntryTypeRegistry.INSTANCE.register(serializer);
		}
	};

	/**
	 * Registers a loot entry type by its serializer.
	 *
	 * @param serializer the loot entry serializer
	 */
	void register(LootEntry.Serializer<?> serializer);
}
