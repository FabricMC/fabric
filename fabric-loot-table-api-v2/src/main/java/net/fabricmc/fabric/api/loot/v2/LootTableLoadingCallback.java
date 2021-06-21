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

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A callback that is called when loot tables are loaded.
 * Use {@link #EVENT} to register instances.
 */
@FunctionalInterface
public interface LootTableLoadingCallback {
	Event<LootTableLoadingCallback> EVENT = EventFactory.createArrayBacked(
			LootTableLoadingCallback.class,
			(listeners) -> (resourceManager, lootManager, id, tableBuilder, setter) -> {
				LootTable[] replacement = new LootTable[1];
				LootTableSetter internalSetter = table -> replacement[0] = table;

				for (LootTableLoadingCallback callback : listeners) {
					callback.onLootTableLoading(resourceManager, lootManager, id, tableBuilder, internalSetter);

					if (replacement[0] != null) {
						setter.set(replacement[0]);
						break;
					}
				}
			}
	);

	/**
	 * Called when a loot table is loading.
	 *
	 * @param resourceManager the server resource manager
	 * @param lootManager     the loot manager
	 * @param id              the loot table ID
	 * @param tableBuilder    a builder of the loot table being loaded
	 * @param setter          a loot table setter for completely replacing the loaded loot table
	 */
	void onLootTableLoading(ResourceManager resourceManager, LootManager lootManager, Identifier id, FabricLootTableBuilder tableBuilder, LootTableSetter setter);

	/**
	 * Used for replacing loot tables in {@link LootTableLoadingCallback#onLootTableLoading(ResourceManager, LootManager, Identifier, FabricLootTableBuilder, LootTableSetter)}.
	 */
	@FunctionalInterface
	interface LootTableSetter {
		/**
		 * Sets the loaded loot table value to the table.
		 *
		 * <p>Calling this method cancels the remaining callbacks of the event.
		 *
		 * @param table the replacement table
		 */
		void set(LootTable table);
	}
}
