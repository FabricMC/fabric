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
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class LootTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Test loot table load event
		// The LootTable.Builder LootPool.Builder methods here should use
		// prebuilt entries and pools to test the injected methods.
		LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original) -> {
			if (Blocks.BLACK_WOOL.getLootTableId().equals(id)) {
				// Replace black wool drops with an iron ingot
				LootPool pool = LootPool.builder()
						.with(ItemEntry.builder(Items.IRON_INGOT).build())
						.build();

				return LootTable.builder().pool(pool).build();
			}

			return null;
		});

		// Test that the event is stopped when the loot table is replaced
		LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original) -> {
			if (Blocks.BLACK_WOOL.getLootTableId().equals(id)) {
				throw new AssertionError("Event should have been stopped from replaced loot table");
			}

			return null;
		});

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, replaced) -> {
			if (Blocks.WHITE_WOOL.getLootTableId().equals(id)) {
				// Add gold ingot with custom name to white wool drops
				LootPool pool = LootPool.builder()
						.with(ItemEntry.builder(Items.GOLD_INGOT).build())
						.conditionally(SurvivesExplosionLootCondition.builder().build())
						.apply(SetNameLootFunction.builder(new LiteralText("Gold from White Wool")).build())
						.build();

				tableBuilder.pool(pool);
			}
		});
	}
}
