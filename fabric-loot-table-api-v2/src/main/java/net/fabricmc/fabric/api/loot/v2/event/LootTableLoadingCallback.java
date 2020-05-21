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

package net.fabricmc.fabric.api.loot.v2.event;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;

/**
 * An event handler that is called when loot tables are loaded.
 * Use {@link #EVENT} to register instances.
 */
@FunctionalInterface
public interface LootTableLoadingCallback {
	@FunctionalInterface
	interface LootTableSetter {
		void set(LootTable table);
	}

	Event<LootTableLoadingCallback> EVENT = EventFactory.createArrayBacked(
			LootTableLoadingCallback.class,
			(listeners) -> (resourceManager, lootManager, id, table, setter) -> {
				for (LootTableLoadingCallback callback : listeners) {
					callback.onLootTableLoading(resourceManager, lootManager, id, table, setter);
				}
			}
	);

	/**
	 * Called when a loot table is loaded.
	 *
	 * @param resourceManager the resource manager
	 * @param lootManager     the loot manager
	 * @param id              the loot table ID
	 * @param table           the loot table builder; can be used to add new entries to the table
	 * @param setter          the loot table setter; can be used to replace the entire loot table
	 */
	void onLootTableLoading(ResourceManager resourceManager, LootManager lootManager, Identifier id, FabricLootTableBuilder table, LootTableSetter setter);
}
