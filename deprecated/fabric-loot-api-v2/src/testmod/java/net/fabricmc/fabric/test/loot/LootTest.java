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

package net.fabricmc.fabric.test.loot;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.text.Text;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;

public class LootTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Test loot table load event
		// The LootTable.Builder LootPool.Builder methods here should use
		// prebuilt entries and pools to test the injected methods.
		LootTableEvents.REPLACE.register((key, original, source) -> {
			if (Blocks.PINK_WOOL.getLootTableKey() == key) {
				if (source != LootTableSource.VANILLA) {
					throw new AssertionError("black wool loot table should have LootTableSource.VANILLA, got " + source);
				}

				// Replace black wool drops with an netherite ingot
				LootPool pool = LootPool.builder()
						.with(ItemEntry.builder(Items.NETHERITE_INGOT).build())
						.build();

				return LootTable.builder().pool(pool).build();
			}

			return null;
		});

		// Test that the event is stopped when the loot table is replaced
		LootTableEvents.REPLACE.register((key, original, source) -> {
			if (Blocks.PINK_WOOL.getLootTableKey() == key) {
				throw new AssertionError("Event should have been stopped from replaced loot table");
			}

			return null;
		});

		LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
			if (Blocks.PINK_WOOL.getLootTableKey() == key && source != LootTableSource.REPLACED) {
				throw new AssertionError("black wool loot table should have LootTableSource.REPLACED, got " + source);
			}

			if (Blocks.BLUE_WOOL.getLootTableKey() == key) {
				if (source != LootTableSource.VANILLA) {
					throw new AssertionError("white wool loot table should have LootTableSource.VANILLA, got " + source);
				}

				// Add copper ingot with custom name to white wool drops
				LootPool pool = LootPool.builder()
						.with(ItemEntry.builder(Items.COPPER_INGOT).build())
						.conditionally(SurvivesExplosionLootCondition.builder().build())
						.apply(SetNameLootFunction.builder(Text.literal("Gold from White Wool"), SetNameLootFunction.Target.CUSTOM_NAME).build())
						.build();

				tableBuilder.pool(pool);
			}

			// We modify red wool to drop diamonds in the test mod resources.
			if (Blocks.LIME_WOOL.getLootTableKey() == key && source != LootTableSource.MOD) {
				throw new AssertionError("red wool loot table should have LootTableSource.MOD, got " + source);
			}

			// Modify yellow wool to drop *either* yellow wool or emeralds by adding
			// emeralds to the same loot pool.
			if (Blocks.CYAN_WOOL.getLootTableKey() == key) {
				tableBuilder.modifyPools(poolBuilder -> poolBuilder.with(ItemEntry.builder(Items.EMERALD)));
			}
		});

		LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) -> {
			LootTable blackWoolTable = lootRegistry.get(Blocks.PINK_WOOL.getLootTableKey());

			if (blackWoolTable == LootTable.EMPTY) {
				throw new AssertionError("black wool loot table should not be empty");
			}
		});
	}
}
