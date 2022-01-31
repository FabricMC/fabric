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

import org.jetbrains.annotations.Nullable;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events for manipulating loot tables.
 */
public final class LootTableEvents {
	/**
	 * This event can be used to replace loot tables.
	 * If a loot table is replaced, the iteration will stop for that loot table.
	 */
	public static final Event<Replace> REPLACE = EventFactory.createArrayBacked(Replace.class, listeners -> (resourceManager, lootManager, id, original, source) -> {
		for (Replace listener : listeners) {
			@Nullable LootTable replaced = listener.replaceLootTable(resourceManager, lootManager, id, original, source);

			if (replaced != null) {
				return replaced;
			}
		}

		return null;
	});

	/**
	 * This event can be used to modify loot tables.
	 */
	public static final Event<Modify> MODIFY = EventFactory.createArrayBacked(Modify.class, listeners -> (resourceManager, lootManager, id, tableBuilder, source) -> {
		for (Modify listener : listeners) {
			listener.modifyLootTable(resourceManager, lootManager, id, tableBuilder, source);
		}
	});

	public interface Replace {
		/**
		 * Replaces loot tables.
		 *
		 * @param resourceManager the server resource manager
		 * @param lootManager     the loot manager
		 * @param id              the loot table ID
		 * @param original        the original loot table
		 * @param source          the source of the original loot table
		 * @return the new loot table, or null if it wasn't replaced
		 */
		@Nullable
		LootTable replaceLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable original, LootTableSource source);
	}

	public interface Modify {
		/**
		 * Called when a loot table is loading to modify loot tables.
		 *
		 * @param resourceManager the server resource manager
		 * @param lootManager     the loot manager
		 * @param id              the loot table ID
		 * @param tableBuilder    a builder of the loot table being loaded
		 * @param source          the source of the loot table
		 */
		void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source);
	}
}
