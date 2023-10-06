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
	private LootTableEvents() {
	}

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
	 * The main use case is to add items to vanilla or mod loot tables (e.g. modded seeds to grass).
	 *
	 * <p>You can also modify loot tables that are created by {@link #REPLACE}.
	 * They have the loot table source {@link LootTableSource#REPLACED}.
	 *
	 * <h2>Example: adding diamonds to the cobblestone loot table</h2>
	 * We'll add a new diamond {@linkplain net.minecraft.loot.LootPool loot pool} to the cobblestone loot table
	 * that will be dropped alongside the original cobblestone loot pool.
	 *
	 * <p>If you want only one of the items to drop, you can use
	 * {@link FabricLootTableBuilder#modifyPools(java.util.function.Consumer)} to add the new item to
	 * the original loot pool instead.
	 *
	 * <pre>
	 * {@code
	 * LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
	 *     // If the loot table is for the cobblestone block and it is not overridden by a user:
	 *     if (Blocks.COBBLESTONE.getLootTableId().equals(id) && source.isBuiltin()) {
	 *         // Create a new loot pool that will hold the diamonds.
	 *         LootPool.Builder pool = LootPool.builder()
	 *             // Add diamonds...
	 *             .with(ItemEntry.builder(Items.DIAMOND))
	 *             // ...only if the block would survive a potential explosion.
	 *             .conditionally(SurvivesExplosionLootCondition.builder());
	 *
	 *         // Add the loot pool to the loot table
	 *         tableBuilder.pool(pool);
	 *     }
	 * });
	 * }
	 * </pre>
	 */
	public static final Event<Modify> MODIFY = EventFactory.createArrayBacked(Modify.class, listeners -> (resourceManager, lootManager, id, tableBuilder, source) -> {
		for (Modify listener : listeners) {
			listener.modifyLootTable(resourceManager, lootManager, id, tableBuilder, source);
		}
	});

	/**
	 * This event can be used for post-processing after all loot tables have been loaded and modified by Fabric.
	 */
	public static final Event<Loaded> ALL_LOADED = EventFactory.createArrayBacked(Loaded.class, listeners -> (resourceManager, lootManager) -> {
		for (Loaded listener : listeners) {
			listener.onLootTablesLoaded(resourceManager, lootManager);
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

	public interface Loaded {
		/**
		 * Called when all loot tables have been loaded and {@link LootTableEvents#REPLACE} and {@link LootTableEvents#MODIFY} have been invoked.
		 *
		 * @param resourceManager the server resource manager
		 * @param lootManager     the loot manager
		 */
		void onLootTablesLoaded(ResourceManager resourceManager, LootManager lootManager);
	}
}
